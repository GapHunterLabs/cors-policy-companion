package dev.gaphunter.corspolicycompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.corspolicycompanion.detect.JavaCorsFinder
import dev.gaphunter.corspolicycompanion.detect.KotlinCorsFinder
import dev.gaphunter.corspolicycompanion.model.CorsHit
import dev.gaphunter.corspolicycompanion.review.ReviewPrompt

/**
 * Warning icon on any `@CrossOrigin(origins = "*", allowCredentials =
 * "true")` annotation -- always a real misconfiguration, since browsers
 * reject this exact combination per the CORS spec itself.
 */
class CorsPolicyLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "Invalid CORS wildcard-origin-plus-credentials"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaCorsFinder.findAll(file)
            "kotlin" -> KotlinCorsFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val leafByHit = hits.associateBy { leafOf(it.annotationElement) }
        for (element in elements) {
            val hit = leafByHit[element] ?: continue
            result.add(buildMarker(element, hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(leaf: PsiElement, hit: CorsHit): LineMarkerInfo<PsiElement> {
        val tooltip = "@CrossOrigin(origins = \"*\", allowCredentials = \"true\") is invalid per the CORS spec -- browsers reject a wildcard origin combined with credentials, so requests silently fail"
        return LineMarkerInfo(
            leaf,
            leaf.textRange,
            CorsIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }

    /** Leaf-anchored, never a composite node -- the annotation element itself is composite, so descend to its own first real leaf token. */
    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
