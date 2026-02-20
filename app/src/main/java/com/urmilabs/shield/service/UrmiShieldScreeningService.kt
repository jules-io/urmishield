package com.urmilabs.shield.service

import android.content.Intent
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import android.content.Context

class UrmiShieldScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val callerNumber = callDetails.handle?.schemeSpecificPart ?: ""

        // 1. STIR/SHAKEN Check (Zero-Latency)
        if (callDetails.callerNumberVerificationStatus == Connection.VERIFICATION_STATUS_FAILED) {
            respondToCall(callDetails, CallResponse.Builder()
                .setDisallowCall(true)
                .setRejectCall(true)
                .setSkipNotification(true)
                .build())
            return
        }

        // 2. Start Intelligence Service (Foreground)
        val intent = Intent(this, IntelligenceService::class.java).apply {
            putExtra("caller_number", callerNumber)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        // 3. Silence Call (Wait for AI)
        respondToCall(callDetails, CallResponse.Builder()
            .setSilenceCall(true)
            .setShouldScreenCallViaTextToSpeech(true)
            .build())
    }
}
