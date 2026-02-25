# Protect Room entities from obfuscation
-keep class com.urmilabs.shield.db.** { *; }

# Protect SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }

# Protect Android components (services, workers) that are referenced by name in Manifest
-keep class com.urmilabs.shield.service.UrmiShieldAccessibilityService { *; }
-keep class com.urmilabs.shield.service.UrmiShieldScreeningService { *; }
-keep class com.urmilabs.shield.worker.ScamPatternWorker { *; }

# Keep MainActivity (referenced in Manifest)
-keep class com.urmilabs.shield.MainActivity { *; }

# Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Kotlin serialization
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Allow obfuscation of com.urmilabs.shield.ai.** (The proprietary logic)
# No -keep rules for com.urmilabs.shield.ai.** means it will be obfuscated.
