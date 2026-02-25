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
class GuardianSettingsViewModel @Inject constructor(
    private val settings: SettingsRepository
) : ViewModel() {

    val guardianNumber = settings.guardianNumber
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveGuardianNumber(number: String) {
        viewModelScope.launch { settings.setGuardianNumber(number) }
    }
}
