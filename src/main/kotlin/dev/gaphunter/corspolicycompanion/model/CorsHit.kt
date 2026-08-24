package dev.gaphunter.corspolicycompanion.model

import com.intellij.psi.PsiElement

/** One `@CrossOrigin(origins = "*", allowCredentials = "true")` finding -- the CORS-spec-invalid combination itself (browsers reject it, so it's always a real misconfiguration, never intentional). */
data class CorsHit(val annotationElement: PsiElement)
