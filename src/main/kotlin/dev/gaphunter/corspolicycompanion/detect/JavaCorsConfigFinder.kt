package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethodCallExpression
import dev.gaphunter.corspolicycompanion.model.CorsHit
import dev.gaphunter.corspolicycompanion.model.CorsHitKind
import dev.gaphunter.corspolicycompanion.model.hasStarElement

/**
 * Finds the `WebMvcConfigurer`-style global CORS registration --
 * `registry.addMapping(...).allowedOrigins("*").allowCredentials(true)`
 * -- the same CORS-spec-invalid combination as `@CrossOrigin`, just
 * expressed as a fluent `CorsRegistration` chain instead of an
 * annotation. Browsers still reject a wildcard origin combined with
 * credentials regardless of which Spring API produced it, so this is
 * always a real misconfiguration, never deliberate.
 */
object JavaCorsConfigFinder {

    fun findAll(file: PsiFile): List<CorsHit> {
        val hits = mutableListOf<CorsHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitExpressionStatement(statement: PsiExpressionStatement) {
                super.visitExpressionStatement(statement)
                val call = statement.expression as? PsiMethodCallExpression ?: return
                riskyCorsRegistration(call)?.let { kind -> hits += CorsHit(statement, kind) }
            }
        })
        return hits
    }

    /** Walks the fluent chain left-to-right, collecting each `.methodName(arg)` call regardless of order. */
    private fun riskyCorsRegistration(rootCall: PsiMethodCallExpression): CorsHitKind? {
        var hasAddMapping = false
        var hasWildcardOrigin = false
        var hasWildcardPattern = false
        var hasCredentialsTrue = false

        var current: PsiMethodCallExpression? = rootCall
        while (current != null) {
            val methodName = current.methodExpression.referenceName
            val args = current.argumentList.expressions
            when (methodName) {
                "addMapping" -> hasAddMapping = true
                "allowedOrigins" -> if (args.any { hasStarElement(it.text) }) hasWildcardOrigin = true
                "allowedOriginPatterns" -> if (args.any { hasStarElement(it.text) }) hasWildcardPattern = true
                "allowCredentials" ->
                    if (args.any { it.text.trim() == "true" }) hasCredentialsTrue = true
            }
            val qualifier = current.methodExpression.qualifierExpression
            current = qualifier as? PsiMethodCallExpression
        }
        if (!hasAddMapping || !hasCredentialsTrue) return null
        return when {
            hasWildcardOrigin -> CorsHitKind.WILDCARD_ORIGIN
            hasWildcardPattern -> CorsHitKind.WILDCARD_PATTERN
            else -> null
        }
    }
}
