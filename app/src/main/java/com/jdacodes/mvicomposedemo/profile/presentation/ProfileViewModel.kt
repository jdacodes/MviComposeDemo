package com.jdacodes.mvicomposedemo.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val navigator: ProfileNavigator
) : ViewModel() {
    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _effect = Channel<ProfileUiEffect>()
    val effect: Flow<ProfileUiEffect> = _effect.receiveAsFlow()

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.DisplayUserDetails -> getCurrentUser()
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading // Set loading state
            try {
                val result = authRepository.getCurrentUser() // Fetch user details
                _state.value = ProfileState.Success(result) // Update state with success
                _effect.send(ProfileUiEffect.ShowToast("User details loaded successfully"))
            } catch (e: Exception) {
                // Handle error, e.g., send an effect to show an error message
                _effect.send(ProfileUiEffect.ShowToast("Error loading user details"))
            }
        }
    }
}

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val navigator: ProfileNavigator,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepository, navigator) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}