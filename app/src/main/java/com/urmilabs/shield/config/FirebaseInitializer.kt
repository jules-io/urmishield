package com.urmilabs.shield.config

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import android.util.Log

/**
 * App Startup library initializer for Firebase.
 * Ensures Firebase initializes as early as possible in the app lifecycle,
 * before any Activity or Service is created.
 *
 * Register in AndroidManifest.xml:
 * <provider
 *     android:name="androidx.startup.InitializationProvider"
 *     android:authorities="${applicationId}.androidx-startup"
 *     android:exported="false"
 *     tools:node="merge">
 *     <meta-data
 *         android:name="com.urmilabs.shield.config.FirebaseInitializer"
 *         android:value="androidx.startup" />
 * </provider>
 */
class FirebaseInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        try {
            // FirebaseApp.initializeApp is safe to call even without google-services.json
            // It will simply not initialize and return null
            val app = FirebaseApp.initializeApp(context)
            if (app != null) {
                // Enable Crashlytics collection
                FirebaseCrashlytics.getInstance().apply {
                    setCrashlyticsCollectionEnabled(true)
                    log("Firebase initialized successfully via App Startup")
                }
                Log.i("FirebaseInitializer", "Firebase initialized successfully")
            } else {
                Log.w("FirebaseInitializer", "Firebase not initialized — google-services.json may be missing")
            }
        } catch (e: Exception) {
            Log.e("FirebaseInitializer", "Firebase initialization failed", e)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
