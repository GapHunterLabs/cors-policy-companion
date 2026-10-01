package dev.gaphunter.corspolicycompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import dev.gaphunter.corspolicycompanion.model.CorsHit
import dev.gaphunter.corspolicycompanion.model.CorsHitKind

class JavaCorsFinderTest : BasePlatformTestCase() {

    fun `test wildcard origin plus true credentials on a method is flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(origins = "*", allowCredentials = "true")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaCorsFinder.findAll(file).size)
    }

    fun `test wildcard origin plus true credentials on a class is flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            @CrossOrigin(origins = "*", allowCredentials = "true")
            class ApiController {
                void getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaCorsFinder.findAll(file).size)
    }

    fun `test wildcard origin with no allowCredentials is not flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(origins = "*")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsFinder.findAll(file).isEmpty())
    }

    fun `test a specific origin with credentials is not flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(origins = "https://app.acmecorp.com", allowCredentials = "true")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsFinder.findAll(file).isEmpty())
    }

    fun `test allowCredentials false with wildcard origin is not flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(origins = "*", allowCredentials = "false")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsFinder.findAll(file).isEmpty())
    }

    fun `test a method with no CrossOrigin annotation at all is never flagged`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                void getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(JavaCorsFinder.findAll(file).isEmpty())
    }

    fun `test the value alias of origins is checked too`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(value = "*", allowCredentials = "true")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(listOf(CorsHitKind.WILDCARD_ORIGIN), JavaCorsFinder.findAll(file).map { it.kind })
    }

    fun `test a wildcard origin pattern with credentials is reported as such`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(originPatterns = "*", allowCredentials = "true")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(listOf(CorsHitKind.WILDCARD_PATTERN), JavaCorsFinder.findAll(file).map { it.kind })
    }

    fun `test a star inside an origin is not the wildcard`() {
        val file = myFixture.configureByText(
            "ApiController.java",
            """
            class ApiController {
                @CrossOrigin(origins = "https://*.example.com", allowCredentials = "true")
                void getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(emptyList<CorsHitKind>(), JavaCorsFinder.findAll(file).map { it.kind })
    }

}
