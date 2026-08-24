package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.PsiFile
import dev.gaphunter.corspolicycompanion.model.CorsHit
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
                findRiskyAnnotation(classOrObject)?.let { hits += CorsHit(it) }
            }

            override fun visitNamedFunction(function: KtNamedFunction) {
                super.visitNamedFunction(function)
                findRiskyAnnotation(function)?.let { hits += CorsHit(it) }
            }
        })
        return hits
    }

    private fun findRiskyAnnotation(owner: KtAnnotated): KtAnnotationEntry? {
        for (entry in owner.annotationEntries) {
            if (entry.shortName?.asString() != "CrossOrigin") continue
            val originsText = argumentText(entry, "origins") ?: continue
            val credentialsText = argumentText(entry, "allowCredentials") ?: continue
            if (originsText.contains("*") && credentialsText.contains("true")) return entry
        }
        return null
    }

    private fun argumentText(entry: KtAnnotationEntry, name: String): String? =
        entry.valueArguments.firstOrNull { it.getArgumentName()?.asName?.asString() == name }
            ?.getArgumentExpression()?.text
}
