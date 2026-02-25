package com.urmilabs.shield.config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.urmilabs.shield.R
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper around Firebase Remote Config for all dynamically configurable values.
 * Replaces hardcoded constants throughout the codebase.
 *
 * All keys have sensible defaults defined in res/xml/remote_config_defaults.xml
 * so the app works even without a Firebase project configured.
 *
 * If Firebase is not initialized (e.g. missing google-services.json), this class
 * gracefully falls back to hardcoded defaults so the app never crashes.
 */
@Singleton
class ShieldRemoteConfig @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig? by lazy {
        try {
            FirebaseRemoteConfig.getInstance().apply {
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(3600) // 1 hour in prod
                    .build()
                setConfigSettingsAsync(configSettings)
                setDefaultsAsync(R.xml.remote_config_defaults)
                fetchAndActivate()
            }
        } catch (e: Exception) {
            Log.w("ShieldRemoteConfig", "Firebase Remote Config unavailable, using defaults", e)
            null
        }
    }

    /** Debounce time for text analysis in the accessibility service (ms) */
    val textAnalysisDebounceMs: Long
        get() = remoteConfig?.getLong("text_analysis_debounce_ms") ?: 300L

    /** Emergency bypass timeout for redial detection (ms) */
    val emergencyBypassTimeoutMs: Long
        get() = remoteConfig?.getLong("emergency_bypass_timeout_ms") ?: 10000L

    /** Worker interval for scam pattern updates (hours) */
    val patternUpdateIntervalHours: Long
        get() = remoteConfig?.getLong("pattern_update_interval_hours") ?: 24L

    /** URL for scam pattern definitions */
    val scamPatternsUrl: String
        get() = remoteConfig?.getString("scam_patterns_url")
            ?: "https://urmilabs.com/scam-patterns.json"

    /** Comma-separated list of supported dialer packages */
    val supportedDialerPackages: Set<String>
        get() {
            val raw = remoteConfig?.getString("supported_dialer_packages")
            return if (!raw.isNullOrBlank()) {
                raw.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
            } else {
                setOf(
                    "com.google.android.dialer",
                    "com.samsung.android.dialer",
                    "com.android.phone"
                )
            }
        }

    /** Force-refresh config from server */
    fun forceRefresh() {
        try {
            remoteConfig?.fetch(0)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    remoteConfig?.activate()
                }
            }
        } catch (e: Exception) {
            Log.w("ShieldRemoteConfig", "Force refresh failed", e)
        }
    }
}
