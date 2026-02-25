package com.urmilabs.shield.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.urmilabs.shield.config.ShieldRemoteConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@HiltWorker
class ScamPatternWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val remoteConfig: ShieldRemoteConfig
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val secureUrl = remoteConfig.scamPatternsUrl
        val patternFile = File(applicationContext.filesDir, "scam_patterns.json")

        try {
            val url = URL(secureUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(patternFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
                return Result.success()
            } else {
                return Result.retry()
            }
        } catch (e: IOException) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
            return Result.retry()
        }
    }
}
