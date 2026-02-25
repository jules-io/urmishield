package com.urmilabs.shield.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.urmilabs.shield.db.NumberListDao
import com.urmilabs.shield.db.NumberListEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumberListViewModel @Inject constructor(
    private val numberListDao: NumberListDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val type: String = savedStateHandle["type"] ?: "WHITELIST"

    val numbers = numberListDao.getNumbersByType(type)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNumber(number: String) {
        if (number.isBlank()) return
        viewModelScope.launch {
            numberListDao.insert(NumberListEntity(number, type, "Manual"))
        }
    }

    fun removeNumber(number: String) {
        viewModelScope.launch {
            numberListDao.delete(number)
        }
    }
}
