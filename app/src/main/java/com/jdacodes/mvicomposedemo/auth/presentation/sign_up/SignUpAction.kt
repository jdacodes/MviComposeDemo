package com.jdacodes.mvicomposedemo.auth.presentation.sign_up

sealed class SignUpAction {
    data class UpdateEmail(val email: String) : SignUpAction()
    data class UpdatePassword(val password: String) : SignUpAction()
    data class UpdateConfirmPassword(val confirmPassword: String) : SignUpAction()
    data class UpdateUsername(val username: String) : SignUpAction()
    data object SubmitSignUp : SignUpAction()
}