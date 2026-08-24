package dev.gaphunter.corspolicycompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import dev.gaphunter.corspolicycompanion.model.CorsHit

/**
 * Finds Java `@CrossOrigin(origins = "*", allowCredentials = "true")`
 * (class- or method-level) -- this exact combination is forbidden by
 * the CORS spec itself (browsers reject a wildcard origin combined with
 * credentials), so it's never a deliberate choice, always a real
 * misconfiguration that silently breaks in the browser (or, worse, a
 * misunderstanding of what the annotation actually does).
 */
object JavaCorsFinder {

    fun findAll(file: PsiFile): List<CorsHit> {
        val hits = mutableListOf<CorsHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitClass(aClass: PsiClass) {
                super.visitClass(aClass)
                findRiskyAnnotation(aClass)?.let { hits += CorsHit(it) }
            }

            override fun visitMethod(method: PsiMethod) {
                super.visitMethod(method)
                findRiskyAnnotation(method)?.let { hits += CorsHit(it) }
            }
        })
        return hits
    }

    private fun findRiskyAnnotation(owner: PsiModifierListOwner): PsiAnnotation? {
        for (annotation in owner.modifierList?.annotations.orEmpty()) {
            if (annotation.nameReferenceElement?.referenceName != "CrossOrigin") continue
            val originsText = annotation.findAttributeValue("origins")?.text ?: continue
            val credentialsText = annotation.findAttributeValue("allowCredentials")?.text ?: continue
            if (originsText.contains("*") && credentialsText.contains("true")) return annotation
        }
        return null
    }
}
