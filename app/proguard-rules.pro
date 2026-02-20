# Protect AI Logic
-keep class com.urmilabs.shield.ai.LiteRTClassifier { *; }
-keep class com.urmilabs.shield.ai.ScamDetector { *; }

# Protect Database Entities
-keep class com.urmilabs.shield.db.** { *; }

# Obfuscate everything else
-repackageclasses ''
-allowaccessmodification
