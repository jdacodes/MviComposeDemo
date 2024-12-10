package com.jdacodes.mvicomposedemo.auth.presentation.splash.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.util.Constants.SPLASH_SCREEN_DURATION
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashViewModel(
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _eventState = savedStateHandle.getLiveData<Boolean>("eventState", false)
    val eventState: Flow<Boolean> = _eventState.asFlow().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = savedStateHandle["eventState"] ?: false
    )

    init {
        viewModelScope.launch {
            delay(SPLASH_SCREEN_DURATION)
            autoLoginUser()
        }
    }

    private fun autoLoginUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .catch { e ->
                    Timber.e("autoLoginUser: ${e.message}")
                    _eventState.value = false
                    _isLoading.value = false
                }
                .collect { user ->
                    _eventState.value = user != null
                    _isLoading.value = false
                }
        }
    }
}