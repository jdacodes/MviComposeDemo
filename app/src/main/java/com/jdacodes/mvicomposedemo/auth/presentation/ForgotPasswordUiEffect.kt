package com.jdacodes.mvicomposedemo.auth.presentation

import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginUiEffect

sealed class ForgotPasswordUiEffect {
    data class ShowToast(val message: String) : ForgotPasswordUiEffect()
    data object NavigateToHome : ForgotPasswordUiEffect()
}