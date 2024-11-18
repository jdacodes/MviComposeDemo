package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

sealed class ForgotPasswordUiEffect {
    data class ShowToast(val message: String) : ForgotPasswordUiEffect()
    data object NavigateToHome : ForgotPasswordUiEffect()
}