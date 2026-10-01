package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.corspolicycompanion.model.CorsHit
import dev.gaphunter.corspolicycompanion.model.CorsHitKind
import dev.gaphunter.corspolicycompanion.model.hasStarElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaCorsConfigFinder] -- same fluent `CorsRegistration` chain, Kotlin call syntax. */
object KotlinCorsConfigFinder {

    fun findAll(file: PsiFile): List<CorsHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<CorsHit>()
        val seen = mutableSetOf<KtDotQualifiedExpression>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                // Only evaluate the outermost link of a chain once, from its top-level statement.
                if (expression.parent is KtDotQualifiedExpression) return
                if (expression in seen) return
                seen += expression
                riskyCorsRegistration(expression)?.let { kind -> hits += CorsHit(expression, kind) }
            }
        })
        return hits
    }

    private fun riskyCorsRegistration(root: KtExpression): CorsHitKind? {
        var hasAddMapping = false
        var hasWildcardOrigin = false
        var hasWildcardPattern = false
        var hasCredentialsTrue = false

        var current: KtExpression? = root
        while (current is KtDotQualifiedExpression) {
            val call = current.selectorExpression as? KtCallExpression
            val methodName = call?.calleeExpression?.text
            val args = call?.valueArguments.orEmpty().mapNotNull { it.getArgumentExpression()?.text }
            when (methodName) {
                "addMapping" -> hasAddMapping = true
                "allowedOrigins" -> if (args.any { hasStarElement(it) }) hasWildcardOrigin = true
                "allowedOriginPatterns" -> if (args.any { hasStarElement(it) }) hasWildcardPattern = true
                "allowCredentials" ->
                    if (args.any { it.trim() == "true" }) hasCredentialsTrue = true
            }
            current = current.receiverExpression
        }
        if (!hasAddMapping || !hasCredentialsTrue) return null
        return when {
            hasWildcardOrigin -> CorsHitKind.WILDCARD_ORIGIN
            hasWildcardPattern -> CorsHitKind.WILDCARD_PATTERN
            else -> null
        }
    }
}
