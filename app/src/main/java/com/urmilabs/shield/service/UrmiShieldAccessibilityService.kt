@file:OptIn(kotlinx.coroutines.FlowPreview::class)

package com.urmilabs.shield.service

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.urmilabs.shield.analytics.ShieldAnalytics
import com.urmilabs.shield.ai.ScamDetector
import com.urmilabs.shield.config.ShieldRemoteConfig
import com.urmilabs.shield.db.CallLogDao
import com.urmilabs.shield.db.CallLogEntity
import com.urmilabs.shield.db.SettingsRepository
import com.urmilabs.shield.ui.components.TranscriptionOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UrmiShieldAccessibilityService : AccessibilityService() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @Inject lateinit var overlayManager: OverlayManager
    @Inject lateinit var scamDetector: ScamDetector
    @Inject lateinit var callLogDao: CallLogDao
    @Inject lateinit var settings: SettingsRepository
    @Inject lateinit var guardianAlerter: GuardianAlerter
    @Inject lateinit var remoteConfig: ShieldRemoteConfig
    @Inject lateinit var analytics: ShieldAnalytics

    // --- State Management ---
    private var isCallActive = false
    private var isBatteryLow = false
    private var lastAlertedKeyword: String? = null
    private var currentCallerInfo: String? = null

    // --- Event Handling ---
    private val textAnalysisFlow = MutableSharedFlow<String>()

    private val batteryStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_BATTERY_LOW -> isBatteryLow = true
                Intent.ACTION_BATTERY_OKAY -> isBatteryLow = false
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_BATTERY_OKAY)
        }
        registerReceiver(batteryStateReceiver, filter, RECEIVER_NOT_EXPORTED)

        setupTextAnalysisFlow()

        analytics.logAccessibilityServiceEnabled()
        Log.d("UrmiShieldAccessibility", "Service connected.")
    }

    private fun setupTextAnalysisFlow() {
        scope.launch {
            textAnalysisFlow
                .debounce(remoteConfig.textAnalysisDebounceMs)
                .map { text ->
                    val suspiciousKeyword = if (!isBatteryLow) scamDetector.detectScam(text) else null
                    Pair(text, suspiciousKeyword)
                }
                .flowOn(Dispatchers.Default)
                .collect { (text, suspiciousKeyword) ->
                    if (!isCallActive) return@collect

                    overlayManager.setContent { 
                        TranscriptionOverlay(
                            transcribedText = text,
                            suspiciousKeyword = suspiciousKeyword
                        )
                    }

                    if (suspiciousKeyword != null && suspiciousKeyword != lastAlertedKeyword) {
                        lastAlertedKeyword = suspiciousKeyword
                        handleScamDetection(suspiciousKeyword)
                    }
                }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val packageName = event.packageName?.toString() ?: ""
                val className = event.className?.toString() ?: ""
                
                val supportedPackages = remoteConfig.supportedDialerPackages
                if (supportedPackages.contains(packageName) && className.contains("call", ignoreCase = true)) {
                    if (!isCallActive) {
                        isCallActive = true
                        lastAlertedKeyword = null
                        overlayManager.showOverlay()
                    }
                } else {
                    if (isCallActive) {
                        isCallActive = false
                        overlayManager.hideOverlay()
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                if (!isCallActive) return
                val text = event.text.joinToString(separator = " ").trim()
                if (text.isNotBlank()) {
                    textAnalysisFlow.tryEmit(text)
                }
            }
        }
    }

    private fun handleScamDetection(reason: String) {
        backgroundScope.launch {
            try {
                logScam("HIGH", reason)
                analytics.logScamAlertTriggered()
                val guardianNumber = settings.guardianNumber.first()
                if (!guardianNumber.isNullOrEmpty()) {
                    guardianAlerter.sendAlert(guardianNumber, reason)
                }
            } catch (e: Exception) {
                Log.e("UrmiShieldAccessibility", "Failed to log or alert for scam.", e)
            }
        }
    }

    private suspend fun logScam(risk: String, reason: String) {
        callLogDao.insert(
            CallLogEntity(
                callerNumber = currentCallerInfo ?: "Unknown",
                riskLevel = risk,
                detectionReason = reason,
                timestamp = System.currentTimeMillis(),
                wasBlocked = false
            )
        )
    }

    override fun onInterrupt() {
        overlayManager.hideOverlay()
        Log.d("UrmiShieldAccessibility", "Service interrupted.")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(batteryStateReceiver)
        overlayManager.hideOverlay()
        scope.cancel()
        backgroundScope.cancel()
    }
}
