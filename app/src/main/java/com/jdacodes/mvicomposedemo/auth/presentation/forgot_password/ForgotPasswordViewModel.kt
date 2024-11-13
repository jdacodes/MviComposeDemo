package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.ForgotPasswordState
import com.jdacodes.mvicomposedemo.auth.presentation.states.SignUpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(ForgotPasswordState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UpdateEmail -> updateEmail(action.email)
            ForgotPasswordAction.SubmitForgotPassword -> submitForgotPassword()
        }
    }

    private fun updateEmail(email: String) {
        val currentState = _state.value as? ForgotPasswordState ?: return
        _state.value = currentState.copy(
            email = email,
            emailError = currentState.validateEmail(email)
        )
        validateForm()
    }

    private fun validateForm() {
        val currentState = _state.value as? ForgotPasswordState ?: return
        _state.value = currentState.copy(
            isValid = currentState.emailError == null &&
                    currentState.email.isNotEmpty()
        )
    }


    private fun submitForgotPassword() {
        val currentState = _state.value as? ForgotPasswordState ?: return
        if (!currentState.isValid) return

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.forgotPassword(
                    currentState.email
                )
                _state.value = AuthState.Sent
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Forgot password failed")
            }
        }
    }
}


class ForgotPasswordViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForgotPasswordViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
