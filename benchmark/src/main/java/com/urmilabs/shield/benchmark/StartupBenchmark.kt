package com.urmilabs.shield.benchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Startup benchmark measuring time-to-initial-display and time-to-full-display.
 * Run on a physical device:
 * ./gradlew :benchmark:pixel6Api31BenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=com.urmilabs.shield.benchmark.StartupBenchmark
 */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupColdNoCompilation() = benchmark(CompilationMode.None())

    @Test
    fun startupColdBaselineProfile() = benchmark(CompilationMode.Partial())

    @Test
    fun startupColdFullCompilation() = benchmark(CompilationMode.Full())

    private fun benchmark(compilationMode: CompilationMode) {
        rule.measureRepeated(
            packageName = "com.urmilabs.shield",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 5
        ) {
            pressHome()
            startActivityAndWait()
        }
    }
}
