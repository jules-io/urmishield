package com.urmilabs.shield.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urmilabs.shield.db.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    val stallingEnabled = settings.stallingEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val deepfakeEnabled = settings.deepfakeEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setStalling(enabled: Boolean) {
        viewModelScope.launch { settings.setStalling(enabled) }
    }

    fun setDeepfake(enabled: Boolean) {
        viewModelScope.launch { settings.setDeepfake(enabled) }
    }
}
