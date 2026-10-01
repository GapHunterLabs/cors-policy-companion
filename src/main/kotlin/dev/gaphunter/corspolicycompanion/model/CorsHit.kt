package dev.gaphunter.corspolicycompanion.model

import com.intellij.psi.PsiElement

/**
 * One wildcard-origin-plus-credentials finding. Two different problems (before 0.2.3 both got the WILDCARD_ORIGIN
 * explanation, which is false for the second one -- found 2026-10-01):
 * - [CorsHitKind.WILDCARD_ORIGIN]: `origins = "*"` / `allowedOrigins("*")` with credentials -- invalid per the CORS
 *   spec, browsers reject it.
 * - [CorsHitKind.WILDCARD_PATTERN]: `originPatterns = "*"` / `allowedOriginPatterns("*")` with credentials -- valid,
 *   and the requests work: every origin is echoed back, so any website can make credentialed requests and read the
 *   responses.
 */
data class CorsHit(val annotationElement: PsiElement, val kind: CorsHitKind = CorsHitKind.WILDCARD_ORIGIN)

enum class CorsHitKind { WILDCARD_ORIGIN, WILDCARD_PATTERN }

// The literal element "*", with its quotes -- so a star inside an origin like https://*.example.com doesn't count.
// (A line comment on purpose: the example origin ends in a star-slash, which would close a KDoc block early.)
private val STAR_ELEMENT = Regex("\"\\*\"")

/** True when the argument text holds the literal string `"*"` as one of its elements. */
fun hasStarElement(argumentText: String): Boolean = STAR_ELEMENT.containsMatchIn(argumentText)
