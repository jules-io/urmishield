package com.urmilabs.shield.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test

class WorkerSchedulerTest {

    private lateinit var mockContext: Context
    private lateinit var mockWorkManager: WorkManager

    @Before
    fun setup() {
        mockContext = mockk(relaxed = true)
        mockWorkManager = mockk(relaxed = true)
        mockkStatic(WorkManager::class)
        every { WorkManager.getInstance(mockContext) } returns mockWorkManager
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `schedules unique periodic work to prevent duplicates`() {
        WorkerScheduler.scheduleScamPatternUpdates(mockContext)

        verify {
            mockWorkManager.enqueueUniquePeriodicWork(
                "scam_pattern_updates",
                ExistingPeriodicWorkPolicy.KEEP,
                any()
            )
        }
    }
}
