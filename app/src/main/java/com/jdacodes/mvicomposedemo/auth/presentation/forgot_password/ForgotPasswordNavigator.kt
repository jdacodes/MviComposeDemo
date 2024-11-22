package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import com.jdacodes.mvicomposedemo.navigation.util.ForgotPasswordRoute
import com.jdacodes.mvicomposedemo.navigation.util.LoginRoute

class ForgotPasswordNavigator(
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
            popUpTo(ForgotPasswordRoute) { inclusive = true }
        }
    }
}