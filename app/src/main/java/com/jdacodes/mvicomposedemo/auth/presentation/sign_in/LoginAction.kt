package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import android.content.Context
import com.facebook.AccessToken

sealed class LoginAction {
    data class UpdateEmail(val email: String) : LoginAction()
    data class UpdatePassword(val password: String) : LoginAction()
    data class SubmitLogin(val context: Context): LoginAction()
    data object SignInWithGoogle: LoginAction()
    data class SignInWithFacebook(val accessToken: AccessToken): LoginAction()

    //Navigation Actions
    data object NavigateToSignUp : LoginAction()
    data object NavigateToForgotPassword : LoginAction()
    data object NavigateToHome : LoginAction()
    data object ReturnToForm : LoginAction()
}