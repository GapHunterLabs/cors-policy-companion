package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.corspolicycompanion.model.CorsHit
import dev.gaphunter.corspolicycompanion.model.CorsHitKind
import dev.gaphunter.corspolicycompanion.model.hasStarElement
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaCorsFinder]. */
object KotlinCorsFinder {

    fun findAll(file: PsiFile): List<CorsHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<CorsHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitClassOrObject(classOrObject: KtClassOrObject) {
                super.visitClassOrObject(classOrObject)
                findRiskyAnnotation(classOrObject)?.let { hits += it }
            }

            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                findRiskyAnnotation(function)?.let { hits += it }
            }
        })
        return hits
    }

    private fun findRiskyAnnotation(owner: KtAnnotated): CorsHit? {
        for (entry in owner.annotationEntries) {
            if (entry.shortName?.asString() != "CrossOrigin") continue
            val credentialsText = argumentText(entry, "allowCredentials") ?: continue
            if (!credentialsText.contains("true")) continue
            // `value` (named or the first positional argument) is the alias of `origins`
            val originsText = argumentText(entry, "origins") ?: argumentText(entry, "value")
                ?: entry.valueArguments.firstOrNull { it.getArgumentName() == null }?.getArgumentExpression()?.text
            if (originsText != null && hasStarElement(originsText)) return CorsHit(entry, CorsHitKind.WILDCARD_ORIGIN)
            val patternsText = argumentText(entry, "originPatterns")
            if (patternsText != null && hasStarElement(patternsText)) return CorsHit(entry, CorsHitKind.WILDCARD_PATTERN)
        }
        return null
    }

    private fun argumentText(entry: KtAnnotationEntry, name: String): String? =
        entry.valueArguments.firstOrNull { it.getArgumentName()?.asName?.asString() == name }
            ?.getArgumentExpression()?.text
}
