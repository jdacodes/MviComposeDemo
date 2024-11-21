package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.ForgotPasswordState
import com.jdacodes.mvicomposedemo.auth.presentation.states.LoginState
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(ForgotPasswordState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _uiEffect = Channel<ForgotPasswordUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    // Store the last valid form state
    private var lastFormState: ForgotPasswordState? = null

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.UpdateEmail -> updateEmail(action.email)
            ForgotPasswordAction.SubmitForgotPassword -> submitForgotPassword()
            ForgotPasswordAction.NavigateToLogin -> navigator.navigateToLogin()
        }
    }

    private fun returnToForm() {
        // Return to the last form state or create a new one
        _state.value = lastFormState ?: LoginState()
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
        lastFormState = _state.value as ForgotPasswordState
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
                if (result) {
                    _state.value = AuthState.Sent
                    _uiEffect.send(ForgotPasswordUiEffect.ShowToast("Email is sent successfully"))
                } else {
                    _uiEffect.send(ForgotPasswordUiEffect.ShowToast("Email is not sent"))
                    returnToForm()
                }

            } catch (e: Exception) {
                _uiEffect.send(ForgotPasswordUiEffect.ShowToast("Forgot password failed"))
                returnToForm()
            }
        }
    }
}


