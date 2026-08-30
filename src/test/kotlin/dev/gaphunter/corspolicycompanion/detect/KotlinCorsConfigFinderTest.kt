package dev.gaphunter.corspolicycompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinCorsConfigFinderTest : BasePlatformTestCase() {

    fun `test wildcard origin plus true credentials in a fluent registration is flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun configureCors(registry: CorsRegistry) {
                    registry.addMapping("/**").allowedOrigins("*").allowCredentials(true)
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinCorsConfigFinder.findAll(file).size)
    }

    fun `test order of the fluent calls does not matter`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun configureCors(registry: CorsRegistry) {
                    registry.addMapping("/**").allowCredentials(true).allowedOrigins("*")
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinCorsConfigFinder.findAll(file).size)
    }

    fun `test a specific origin with credentials is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun configureCors(registry: CorsRegistry) {
                    registry.addMapping("/**").allowedOrigins("https://app.acmecorp.com").allowCredentials(true)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test wildcard origin with no allowCredentials call is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun configureCors(registry: CorsRegistry) {
                    registry.addMapping("/**").allowedOrigins("*")
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test allowCredentials false with wildcard origin is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun configureCors(registry: CorsRegistry) {
                    registry.addMapping("/**").allowedOrigins("*").allowCredentials(false)
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated fluent call chain is never flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.kt",
            """
            class WebConfig {
                fun build() {
                    val sb = StringBuilder()
                    sb.append("*").append("true")
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsConfigFinder.findAll(file).isEmpty())
    }
}
