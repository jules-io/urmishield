package com.urmilabs.shield.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkerScheduler {

    private const val WORK_NAME = "scam_pattern_updates"

    fun scheduleScamPatternUpdates(context: Context, intervalHours: Long = 24L) {
        val workManager = WorkManager.getInstance(context)
        val periodicWorkRequest = PeriodicWorkRequestBuilder<ScamPatternWorker>(
            intervalHours, TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
    }
}
