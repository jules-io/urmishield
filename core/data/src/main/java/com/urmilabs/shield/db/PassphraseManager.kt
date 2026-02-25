package com.urmilabs.shield.db

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import java.security.SecureRandom

object PassphraseManager {
    private const val PREFS_FILE = "secure_prefs"
    private const val KEY_ALIAS = "db_passphrase"

    fun getPassphrase(context: Context): ByteArray {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        val sharedPreferences = EncryptedSharedPreferences.create(
            PREFS_FILE,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        var encodedKey = sharedPreferences.getString(KEY_ALIAS, null)
        if (encodedKey == null) {
            // Generate a new 32-byte key
            val keyBytes = ByteArray(32)
            SecureRandom().nextBytes(keyBytes)
            encodedKey = Base64.encodeToString(keyBytes, Base64.NO_WRAP)
            sharedPreferences.edit().putString(KEY_ALIAS, encodedKey).apply()
        }

        return Base64.decode(encodedKey, Base64.NO_WRAP)
    }
}
