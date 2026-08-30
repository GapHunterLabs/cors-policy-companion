package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.corspolicycompanion.model.CorsHit
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
                if (isRiskyCorsRegistration(expression)) hits += CorsHit(expression)
            }
        })
        return hits
    }

    private fun isRiskyCorsRegistration(root: KtExpression): Boolean {
        var hasAddMapping = false
        var hasWildcardOrigin = false
        var hasCredentialsTrue = false

        var current: KtExpression? = root
        while (current is KtDotQualifiedExpression) {
            val call = current.selectorExpression as? KtCallExpression
            val methodName = call?.calleeExpression?.text
            val args = call?.valueArguments.orEmpty().mapNotNull { it.getArgumentExpression()?.text }
            when (methodName) {
                "addMapping" -> hasAddMapping = true
                "allowedOrigins", "allowedOriginPatterns" ->
                    if (args.any { it.contains("*") }) hasWildcardOrigin = true
                "allowCredentials" ->
                    if (args.any { it.trim() == "true" }) hasCredentialsTrue = true
            }
            current = current.receiverExpression
        }
        return hasAddMapping && hasWildcardOrigin && hasCredentialsTrue
    }
}
