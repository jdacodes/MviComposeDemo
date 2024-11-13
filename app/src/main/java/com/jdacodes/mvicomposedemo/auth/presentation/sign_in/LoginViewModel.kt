package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel(

) {
    private val _state = MutableStateFlow<AuthState>(LoginState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateEmail(action.email)
            is LoginAction.UpdatePassword -> updatePassword(action.password)
            is LoginAction.SubmitLogin -> submitLogin(action.context)
            LoginAction.SignInWithGoogle -> signInWithGoogle()
            is LoginAction.SignInWithFacebook -> signInWithFacebook(action.accessToken)
        }
    }

    private fun updateEmail(email: String) {
        val currentState = _state.value as? LoginState ?: return
        _state.value = currentState.copy(
            email = email,
            emailError = currentState.validateEmail(email)
        )
        validateForm()
    }

    private fun updatePassword(password: String) {
        val currentState = _state.value as? LoginState ?: return
        _state.value = currentState.copy(
            password = password,
            passwordError = currentState.validatePassword(password)
        )
        validateForm()
    }

    private fun validateForm() {
        val currentState = _state.value as? LoginState ?: return
        _state.value = currentState.copy(
            isValid = currentState.emailError == null &&
                    currentState.passwordError == null &&
                    currentState.email.isNotEmpty() &&
                    currentState.password.isNotEmpty()
        )
    }

    private fun submitLogin(context: Context) {
        val currentState = _state.value as? LoginState ?: return
        if (!currentState.isValid) return

        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.login(
                    context,
                    currentState.email,
                    currentState.password
                )
                _state.value = AuthState.Success(result)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.signInWithGoogle()
                _state.value = AuthState.Success(result)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Login with Google failed")
            }
        }
    }

    private fun signInWithFacebook(accessToken: AccessToken) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.signInWithFacebook(accessToken)
                _state.value = AuthState.Success(result)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Login with Facebook failed")
            }
        }
    }
}

class LoginViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}