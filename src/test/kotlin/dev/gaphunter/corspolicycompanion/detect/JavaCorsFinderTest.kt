package dev.gaphunter.corspolicycompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

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
}
