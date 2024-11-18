package com.jdacodes.mvicomposedemo.auth.presentation.sign_up

sealed class SignUpUiEffect {
    data class ShowToast(val message: String) : SignUpUiEffect()
}