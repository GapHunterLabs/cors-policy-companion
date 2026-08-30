package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiExpressionStatement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethodCallExpression
import dev.gaphunter.corspolicycompanion.model.CorsHit

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
                if (isRiskyCorsRegistration(call)) hits += CorsHit(statement)
            }
        })
        return hits
    }

    /** Walks the fluent chain left-to-right, collecting each `.methodName(arg)` call regardless of order. */
    private fun isRiskyCorsRegistration(rootCall: PsiMethodCallExpression): Boolean {
        var hasAddMapping = false
        var hasWildcardOrigin = false
        var hasCredentialsTrue = false

        var current: PsiMethodCallExpression? = rootCall
        while (current != null) {
            val methodName = current.methodExpression.referenceName
            val args = current.argumentList.expressions
            when (methodName) {
                "addMapping" -> hasAddMapping = true
                "allowedOrigins", "allowedOriginPatterns" ->
                    if (args.any { it.text.contains("*") }) hasWildcardOrigin = true
                "allowCredentials" ->
                    if (args.any { it.text.trim() == "true" }) hasCredentialsTrue = true
            }
            val qualifier = current.methodExpression.qualifierExpression
            current = qualifier as? PsiMethodCallExpression
        }
        return hasAddMapping && hasWildcardOrigin && hasCredentialsTrue
    }
}
