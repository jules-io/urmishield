package com.urmilabs.shield.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.urmilabs.shield.R
import com.urmilabs.shield.ai.AudioRecorder
import com.urmilabs.shield.ai.LiteRTClassifier
import com.urmilabs.shield.ai.ScamDetector
import kotlinx.coroutines.*

class IntelligenceService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private lateinit var audioRecorder: AudioRecorder
    private lateinit var liteRTClassifier: LiteRTClassifier
    private lateinit var scamDetector: ScamDetector
    private lateinit var guardianAlerter: GuardianAlerter

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        audioRecorder = AudioRecorder()
        liteRTClassifier = LiteRTClassifier(this)
        scamDetector = ScamDetector(this)
        guardianAlerter = GuardianAlerter(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val callerNumber = intent?.getStringExtra("caller_number") ?: "Unknown"

        scope.launch {
            if (shouldThrottleAI()) {
                // Low Battery Mode: Use Regex Only
                scamDetector.analyze(callerNumber, "Battery Saver Mode - Regex Only")
            } else {
                // Full AI Mode
                startFullAnalysis(callerNumber)
            }
        }
        return START_NOT_STICKY
    }

    private fun shouldThrottleAI(): Boolean {
        val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager

        return batteryLevel < 15 || powerManager.isPowerSaveMode
    }

    private suspend fun startFullAnalysis(callerNumber: String) {
        audioRecorder.startRecording { buffer ->
            // 1. Deepfake Check
            val deepfakeScore = liteRTClassifier.detectDeepfake(buffer)
            if (deepfakeScore > 0.8f) {
                guardianAlerter.sendAlert(callerNumber, "Deepfake Detected")
            }

            // 2. Scam Intent (Simulation for now as we don't have SpeechRecognizer in this snippet)
            // In real app, this feeds SpeechRecognizer output to ScamDetector
        }
    }

    private fun createNotification(): Notification {
        val channelId = "ShieldService"
        val channel = NotificationChannel(channelId, "Shield Active", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Urmi Shield Active")
            .setContentText("Protecting you from scams...")
            .setSmallIcon(R.drawable.ic_shield_logo)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecorder.stopRecording()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
