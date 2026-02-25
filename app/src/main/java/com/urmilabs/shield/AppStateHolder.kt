package com.urmilabs.shield

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Hilt-managed holder for global, observable application state.
 * Replaces the old AppState singleton object for proper DI and testability.
 */
@Singleton
class AppStateHolder @Inject constructor() {
    private val _isAiModelAvailable = MutableStateFlow(true)
    val isAiModelAvailable: StateFlow<Boolean> = _isAiModelAvailable.asStateFlow()

    fun setAiModelAvailable(available: Boolean) {
        _isAiModelAvailable.value = available
    }
}
