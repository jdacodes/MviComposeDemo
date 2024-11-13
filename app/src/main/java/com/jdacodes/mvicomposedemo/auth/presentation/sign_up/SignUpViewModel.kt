package com.jdacodes.mvicomposedemo.auth.presentation.sign_up

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.SignUpState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(SignUpState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.UpdateEmail -> updateEmail(action.email)
            is SignUpAction.UpdatePassword -> updatePassword(action.password)
            SignUpAction.SubmitSignUp -> submitSignUp()
            is SignUpAction.UpdateConfirmPassword -> updateConfirmPassword(action.confirmPassword)
            is SignUpAction.UpdateUsername -> updateUsername(action.username)
        }
    }


    private fun updateEmail(email: String) {
        val currentState = _state.value as? SignUpState ?: return
        _state.value = currentState.copy(
            email = email,
            emailError = currentState.validateEmail(email)
        )
        validateForm()
    }

    private fun updatePassword(password: String) {
        val currentState = _state.value as? SignUpState ?: return
        _state.value = currentState.copy(
            password = password,
            passwordError = currentState.validatePassword(password),
            // Revalidate confirm password when password changes
            confirmPasswordError = currentState.validateConfirmPassword(currentState.confirmPassword, password)
        )
        validateForm()
    }

    private fun updateConfirmPassword(confirm: String) {
        val currentState = _state.value as? SignUpState ?: return
        _state.value = currentState.copy(
            confirmPassword = confirm,
            confirmPasswordError = currentState.validateConfirmPassword(confirm, currentState.password)
        )
        validateForm()
    }

    private fun updateUsername(username: String) {
        val currentState = _state.value as? SignUpState ?: return
        _state.value = currentState.copy(
            username = username,
            usernameError = currentState.validateUsername(username)
        )
        validateForm()
    }

    private fun validateForm() {
        val currentState = _state.value as? SignUpState ?: return
        _state.value = currentState.copy(
            isValid = currentState.emailError == null &&
                    currentState.passwordError == null &&
                    currentState.confirmPasswordError == null &&
                    currentState.usernameError == null &&
                    currentState.email.isNotEmpty() &&
                    currentState.password.isNotEmpty() &&
                    currentState.confirmPassword.isNotEmpty() &&
                    currentState.username.isNotEmpty() &&
                    currentState.password == currentState.confirmPassword
        )
    }


    private fun submitSignUp() {
        val currentState = _state.value as? SignUpState ?: return
        if (!currentState.isValid) return

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.signUp(
                    currentState.email,
                    currentState.password,
                    currentState.username
                )
                _state.value = AuthState.Success(result)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }
}

class SignUpViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SignUpViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}