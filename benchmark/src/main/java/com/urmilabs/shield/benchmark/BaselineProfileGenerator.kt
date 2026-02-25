package com.urmilabs.shield.benchmark

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a Baseline Profile for the critical user journey.
 * Run this on a physical device to generate the profile:
 * ./gradlew :benchmark:pixel6Api31BenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=com.urmilabs.shield.benchmark.BaselineProfileGenerator
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(
            packageName = "com.urmilabs.shield",
            maxIterations = 5,
            stableIterations = 3
        ) {
            // Cold start the app
            pressHome()
            startActivityAndWait()

            // Navigate through the critical user journey
            // The app starts with onboarding, then dashboard
            // This generates profile data for the hot path
        }
    }
}
