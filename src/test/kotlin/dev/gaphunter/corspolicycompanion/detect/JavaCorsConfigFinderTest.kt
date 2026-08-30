package dev.gaphunter.corspolicycompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaCorsConfigFinderTest : BasePlatformTestCase() {

    fun `test wildcard origin plus true credentials in a fluent registration is flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("*").allowCredentials(true);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaCorsConfigFinder.findAll(file).size)
    }

    fun `test order of the fluent calls does not matter`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowCredentials(true).allowedOrigins("*");
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaCorsConfigFinder.findAll(file).size)
    }

    fun `test allowedOriginPatterns wildcard plus credentials is flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOriginPatterns("*").allowCredentials(true);
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaCorsConfigFinder.findAll(file).size)
    }

    fun `test a specific origin with credentials is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("https://app.acmecorp.com").allowCredentials(true);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test wildcard origin with no allowCredentials call is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("*");
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test allowCredentials false with wildcard origin is not flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void configureCors(CorsRegistry registry) {
                    registry.addMapping("/**").allowedOrigins("*").allowCredentials(false);
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsConfigFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated fluent call chain is never flagged`() {
        val file = myFixture.configureByText(
            "WebConfig.java",
            """
            class WebConfig {
                void build() {
                    StringBuilder sb = new StringBuilder();
                    sb.append("*").append("true");
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsConfigFinder.findAll(file).isEmpty())
    }
}
