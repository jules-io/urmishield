package com.urmilabs.shield.db

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    companion object {
        val STALLING_ENABLED = booleanPreferencesKey("stalling_enabled")
        val DEEPFAKE_ENABLED = booleanPreferencesKey("deepfake_enabled")
        val SENSITIVITY = intPreferencesKey("sensitivity")
        val GUARDIAN_NUMBER = stringPreferencesKey("guardian_number")
    }

    val stallingEnabled: Flow<Boolean> = context.dataStore.data.map { it[STALLING_ENABLED] ?: true }
    val deepfakeEnabled: Flow<Boolean> = context.dataStore.data.map { it[DEEPFAKE_ENABLED] ?: true }
    val sensitivity: Flow<Int> = context.dataStore.data.map { it[SENSITIVITY] ?: 50 } // 0-100
    val guardianNumber: Flow<String?> = context.dataStore.data.map { it[GUARDIAN_NUMBER] }

    suspend fun setStalling(enabled: Boolean) {
        context.dataStore.edit { it[STALLING_ENABLED] = enabled }
    }

    suspend fun setDeepfake(enabled: Boolean) {
        context.dataStore.edit { it[DEEPFAKE_ENABLED] = enabled }
    }

    suspend fun setSensitivity(value: Int) {
        context.dataStore.edit { it[SENSITIVITY] = value }
    }

    suspend fun setGuardianNumber(number: String) {
        context.dataStore.edit { it[GUARDIAN_NUMBER] = number }
    }
}
