package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

sealed class LoginUiEffect {
    data class ShowToast(val message: String) : LoginUiEffect()
    data object NavigateToHome : LoginUiEffect()
}