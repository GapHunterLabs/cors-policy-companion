package dev.gaphunter.corspolicycompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinCorsFinderTest : BasePlatformTestCase() {

    fun `test wildcard origin plus true credentials on a function is flagged`() {
        val file = myFixture.configureByText(
            "ApiController.kt",
            """
            class ApiController {
                @CrossOrigin(origins = "*", allowCredentials = "true")
                fun getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinCorsFinder.findAll(file).size)
    }

    fun `test wildcard origin plus true credentials on a class is flagged`() {
        val file = myFixture.configureByText(
            "ApiController.kt",
            """
            @CrossOrigin(origins = "*", allowCredentials = "true")
            class ApiController {
                fun getData() { }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinCorsFinder.findAll(file).size)
    }

    fun `test a specific origin with credentials is not flagged`() {
        val file = myFixture.configureByText(
            "ApiController.kt",
            """
            class ApiController {
                @CrossOrigin(origins = "https://app.acmecorp.com", allowCredentials = "true")
                fun getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsFinder.findAll(file).isEmpty())
    }

    fun `test a function with no CrossOrigin annotation at all is never flagged`() {
        val file = myFixture.configureByText(
            "ApiController.kt",
            """
            class ApiController {
                fun getData() { }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinCorsFinder.findAll(file).isEmpty())
    }
}
