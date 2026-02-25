package com.urmilabs.shield.ai

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ScamDetectorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var mockContext: Context
    private lateinit var scamDetector: ScamDetector

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        every { mockContext.filesDir } returns tempFolder.root
        scamDetector = ScamDetector(mockContext)
    }

    @Test
    fun `detects IRS scam keyword from default list`() {
        val text = "This is a call from the IRS regarding your tax filing."
        assertEquals("IRS", scamDetector.detectScam(text))
    }

    @Test
    fun `detects gift card scam keyword from default list`() {
        val text = "You need to pay with a Google Play Card to resolve this."
        assertEquals("Google Play Card", scamDetector.detectScam(text))
    }

    @Test
    fun `returns null for clean text`() {
        val text = "Hello, how are you today?"
        assertNull(scamDetector.detectScam(text))
    }

    @Test
    fun `loads custom patterns from file and detects keyword`() {
        // Arrange: Create a real temporary file with custom patterns
        val customPatterns = "[\"CustomScam1\", \"CustomScam2\"]"
        val patternFile = tempFolder.newFile("scam_patterns.json")
        patternFile.writeText(customPatterns)

        // Act: Re-initialize the detector to force it to read the new file
        val newScamDetector = ScamDetector(mockContext)
        val text = "This is a test for CustomScam1."
        val result = newScamDetector.detectScam(text)

        // Assert
        assertEquals("CustomScam1", result)
    }
}
