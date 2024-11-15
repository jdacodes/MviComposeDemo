package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.core.presentation.ForgotPasswordRoute
import com.jdacodes.mvicomposedemo.core.presentation.HomeRoute
import com.jdacodes.mvicomposedemo.core.presentation.LoginRoute
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import com.jdacodes.mvicomposedemo.core.presentation.SignUpRoute

class LoginNavigator(
    private val navController: NavController
) : Navigator {
    override fun navigateToSignUp() {
        navController.navigate(SignUpRoute) {
            popUpTo(LoginRoute) { inclusive = false }
        }
    }

    override fun navigateToForgotPassword() {
        navController.navigate(ForgotPasswordRoute) {
            popUpTo(LoginRoute) { inclusive = false }
        }
    }

    override fun navigateToHome() {
        navController.navigate(HomeRoute) {
            popUpTo(LoginRoute) { inclusive = true }
        }
    }

    override fun navigateToLogin() {
        TODO("Not yet implemented")
    }
}