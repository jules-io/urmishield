package com.urmilabs.shield.ui.screens

import android.app.Application
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import com.urmilabs.shield.AppStateHolder
import com.urmilabs.shield.service.UrmiShieldAccessibilityService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SystemStatusViewModel @Inject constructor(
    application: Application,
    private val appStateHolder: AppStateHolder
) : AndroidViewModel(application) {

    val isAiModelAvailable: StateFlow<Boolean> = appStateHolder.isAiModelAvailable

    fun isAccessibilityServiceEnabled(): Boolean {
        val context = getApplication<Application>()
        val service = context.packageName + "/" + UrmiShieldAccessibilityService::class.java.canonicalName
        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(service, ignoreCase = false) == true
    }

    fun getLastPatternUpdateTime(): String {
        val context = getApplication<Application>()
        val patternFile = File(context.filesDir, "scam_patterns.json")
        return if (patternFile.exists()) {
            val lastModified = patternFile.lastModified()
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date(lastModified))
        } else {
            "Never"
        }
    }
}
