package com.urmilabs.shield.ai

import android.content.Context
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.regex.Pattern

class ScamDetector(private val context: Context) {
    private var scamPatterns: List<Pattern> = emptyList()

    init {
        loadScamPatterns()
    }

    private fun loadScamPatterns() {
        val patternFile = File(context.filesDir, "scam_patterns.json")
        if (!patternFile.exists()) {
            // Pre-load with default patterns if the file doesn't exist
            scamPatterns = loadDefaultPatterns()
            return
        }

        try {
            val jsonString = FileInputStream(patternFile).bufferedReader().use { it.readText() }
            val patterns = Json.decodeFromString<List<String>>(jsonString)
            scamPatterns = patterns.map { Pattern.compile(".*(" + Pattern.quote(it) + ").*", Pattern.CASE_INSENSITIVE) }
        } catch (e: Exception) { // Catch broader exceptions from parsing
            e.printStackTrace()
            // Fallback to default patterns if loading or parsing fails
            scamPatterns = loadDefaultPatterns()
        }
    }

    private fun loadDefaultPatterns(): List<Pattern> {
        return listOf(
            Pattern.compile(".*(IRS|Internal Revenue Service).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Social Security|SSN|Social Security Number).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Gift Card|Target Gift Card|Google Play Card).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Bail|Jail|Warrant|Arrest).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Wire Transfer|Western Union|MoneyGram).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Grandson|Granddaughter|Emergency|Accident).*", Pattern.CASE_INSENSITIVE),
            Pattern.compile(".*(Refund|Overpayment|Bank Account).*", Pattern.CASE_INSENSITIVE)
        )
    }

    fun detectScam(text: String): String? {
        for (pattern in scamPatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1) // Return the matched keyword
            }
        }
        return null
    }
}
