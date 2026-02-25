package com.urmilabs.shield.service

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import com.urmilabs.shield.analytics.ShieldAnalytics
import javax.inject.Inject

class GuardianAlerter @Inject constructor(
    private val context: Context,
    private val analytics: ShieldAnalytics
) {
    fun sendAlert(guardianNumber: String, scamDetails: String) {
        if (guardianNumber.isEmpty()) return

        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
            val message = context.getString(com.urmilabs.shield.R.string.urmi_shield_alert, scamDetails)
            smsManager.sendTextMessage(guardianNumber, null, message, null, null)
            analytics.logGuardianAlertSent()
            Log.d("GuardianAlerter", "Alert sent to $guardianNumber")
        } catch (e: Exception) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
            Log.e("GuardianAlerter", "Failed to send SMS", e)
        }
    }
}
