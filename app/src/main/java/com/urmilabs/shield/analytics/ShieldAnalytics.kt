package com.urmilabs.shield.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Privacy-preserving analytics logger.
 * Logs completely anonymous events with NO PII, NO text content, NO phone numbers.
 *
 * Events logged:
 * - scam_alert_triggered: a scam keyword was detected during a call
 * - accessibility_service_enabled: user enabled the accessibility service
 * - guardian_alert_sent: an SMS alert was sent to a guardian
 * - app_onboarding_completed: user completed the onboarding flow
 */
@Singleton
class ShieldAnalytics @Inject constructor() {

    private var analytics: FirebaseAnalytics? = null
    private var isOptedIn = false

    /**
     * Initialize analytics with user consent. Must be called before any events are logged.
     * Analytics is disabled by default and only enabled when the user explicitly opts in.
     */
    fun initialize(context: Context, optedIn: Boolean) {
        isOptedIn = optedIn
        if (optedIn) {
            analytics = FirebaseAnalytics.getInstance(context).apply {
                setAnalyticsCollectionEnabled(true)
            }
        }
    }

    fun setOptIn(optedIn: Boolean) {
        isOptedIn = optedIn
        analytics?.setAnalyticsCollectionEnabled(optedIn)
    }

    fun logScamAlertTriggered() {
        logEvent("scam_alert_triggered")
    }

    fun logAccessibilityServiceEnabled() {
        logEvent("accessibility_service_enabled")
    }

    fun logGuardianAlertSent() {
        logEvent("guardian_alert_sent")
    }

    fun logOnboardingCompleted() {
        logEvent("app_onboarding_completed")
    }

    private fun logEvent(eventName: String, params: Bundle? = null) {
        if (!isOptedIn) return
        analytics?.logEvent(eventName, params)
    }
}
