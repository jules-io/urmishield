# Protect Room entities, DAOs, and Database from obfuscation
-keep class com.urmilabs.shield.db.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Protect SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
