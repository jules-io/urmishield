package com.urmilabs.shield.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urmilabs.shield.db.CallLogDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val callLogDao: CallLogDao
) : ViewModel() {

    val totalScreenedCount = callLogDao.getTotalScreenedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val blockedCount = callLogDao.getBlockedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}
