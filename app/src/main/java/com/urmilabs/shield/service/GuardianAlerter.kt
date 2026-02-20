package com.urmilabs.shield.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.telephony.SmsManager

class GuardianAlerter(private val context: Context) {

    fun sendAlert(scammerNumber: String, reason: String) {
        val guardianNumber = "5551234567" // Retrieve from Settings in real app
        val message = "Urmi Shield flagged a call from $scammerNumber. Reason: $reason."

        try {
            // Attempt Direct SMS
            val smsManager = context.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(guardianNumber, null, message, null, null)
        } catch (e: SecurityException) {
            // Fallback: Intent
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$guardianNumber")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
