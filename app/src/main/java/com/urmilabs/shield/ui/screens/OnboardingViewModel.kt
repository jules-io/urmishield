package com.urmilabs.shield.ui.screens

import android.app.Application
import android.app.role.RoleManager
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.urmilabs.shield.analytics.ShieldAnalytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
    private val analytics: ShieldAnalytics
) : AndroidViewModel(application) {

    private val _audioGranted = MutableStateFlow(checkAudioPermission())
    val audioGranted: StateFlow<Boolean> = _audioGranted.asStateFlow()

    private val _overlayGranted = MutableStateFlow(checkOverlayPermission())
    val overlayGranted: StateFlow<Boolean> = _overlayGranted.asStateFlow()

    private val _roleGranted = MutableStateFlow(checkRolePermission())
    val roleGranted: StateFlow<Boolean> = _roleGranted.asStateFlow()

    fun refreshPermissions() {
        _audioGranted.value = checkAudioPermission()
        _overlayGranted.value = checkOverlayPermission()
        _roleGranted.value = checkRolePermission()
    }

    fun onAudioPermissionResult(granted: Boolean) {
        _audioGranted.value = granted
    }

    fun onOverlayPermissionResult() {
        _overlayGranted.value = checkOverlayPermission()
    }

    fun onRolePermissionResult() {
        _roleGranted.value = checkRolePermission()
    }

    val allPermissionsGranted: Boolean
        get() = _audioGranted.value && _overlayGranted.value && _roleGranted.value

    fun onOnboardingComplete() {
        analytics.logOnboardingCompleted()
    }

    private fun checkAudioPermission(): Boolean {
        val context = getApplication<Application>()
        return ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(getApplication())
    }

    private fun checkRolePermission(): Boolean {
        val context = getApplication<Application>()
        val roleManager = context.getSystemService(Context.ROLE_SERVICE) as RoleManager
        return roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }
}
