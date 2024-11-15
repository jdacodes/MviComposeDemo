package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

sealed class ForgotPasswordAction {
    data class UpdateEmail(val email: String) : ForgotPasswordAction()
    data object SubmitForgotPassword : ForgotPasswordAction()

    //Navigation Actions
    data object NavigateToLogin : ForgotPasswordAction()
}