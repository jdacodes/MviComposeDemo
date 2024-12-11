package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.facebook.AccessToken
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.LoginState
import com.jdacodes.mvicomposedemo.navigation.util.Navigator
import com.jdacodes.mvicomposedemo.navigation.util.navigateLoginToForgotPasswordRoute
import com.jdacodes.mvicomposedemo.navigation.util.navigateLoginToHomeGraph
import com.jdacodes.mvicomposedemo.navigation.util.navigateLoginToSignUpRoute
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _state = MutableStateFlow<AuthState>(LoginState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _uiEffect = Channel<LoginUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    // Store the last valid form state
    private var lastFormState: LoginState? = null

    // TODO: Use currentUser to check the user if it is logged in or not, navigate accordingly
    // Expose the current user as a StateFlow
    val currentUser: StateFlow<User?> = authRepository.getCurrentUser()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.UpdateEmail -> updateEmail(action.email)
            is LoginAction.UpdatePassword -> updatePassword(action.password)
            is LoginAction.SubmitLogin -> submitLogin(action.context)
            LoginAction.SignInWithGoogle -> signInWithGoogle()
            is LoginAction.SignInWithFacebook -> signInWithFacebook(action.accessToken)
            LoginAction.NavigateToForgotPassword -> navigator.navigateLoginToForgotPasswordRoute()
            LoginAction.NavigateToHome -> navigator.navigateLoginToHomeGraph()
            LoginAction.NavigateToSignUp -> navigator.navigateLoginToSignUpRoute()
            LoginAction.ReturnToForm -> returnToForm()
        }
    }

    private fun returnToForm() {
        // Return to the last form state or create a new one
        _state.value = lastFormState ?: LoginState()
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
        lastFormState = _state.value as LoginState
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
                _uiEffect.send(LoginUiEffect.ShowToast("Login successful!"))
            } catch (e: Exception) {
                _uiEffect.send(LoginUiEffect.ShowToast(e.message ?: "Login failed"))
                returnToForm()
            }
        }
    }

    private fun signInWithGoogle() {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.signInWithGoogle()
                _state.value = AuthState.Success(result)
                _uiEffect.send(LoginUiEffect.ShowToast("Login successful!"))
            } catch (e: Exception) {
                _uiEffect.send(LoginUiEffect.ShowToast(e.message ?: "Login with Google failed"))
                returnToForm()
            }
        }
    }

    private fun signInWithFacebook(accessToken: AccessToken) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val result = authRepository.signInWithFacebook(accessToken)
                _state.value = AuthState.Success(result)
                _uiEffect.send(LoginUiEffect.ShowToast("Login successful!"))
            } catch (e: Exception) {
                _uiEffect.send(LoginUiEffect.ShowToast(e.message ?: "Login with Facebook failed"))
                returnToForm()
            }
        }
    }
}

