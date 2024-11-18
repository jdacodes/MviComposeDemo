package com.jdacodes.mvicomposedemo.auth.presentation.sign_up

import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.core.presentation.LoginRoute
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import com.jdacodes.mvicomposedemo.core.presentation.SignUpRoute

class SignUpNavigator(
    private val navController: NavController
) : Navigator {
    override fun navigateToSignUp() {
        TODO("Not yet implemented")
    }

    override fun navigateToForgotPassword() {
        TODO("Not yet implemented")
    }

    override fun navigateToHome() {
        TODO("Not yet implemented")
    }

    override fun navigateToLogin() {
        navController.navigate(LoginRoute) {
            popUpTo(SignUpRoute) { inclusive = true }
        }
    }
}