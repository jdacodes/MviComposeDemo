package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import com.jdacodes.mvicomposedemo.navigation.util.ForgotPasswordRoute
import com.jdacodes.mvicomposedemo.navigation.util.HomeGraph
import com.jdacodes.mvicomposedemo.navigation.util.LoginRoute
import com.jdacodes.mvicomposedemo.navigation.util.ProfileRoute
import com.jdacodes.mvicomposedemo.navigation.util.SignUpRoute

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
        navController.navigate(HomeGraph) {
            popUpTo(LoginRoute) { inclusive = true }
        }
    }

    override fun navigateToLogin() {
        TODO("Not yet implemented")
    }
}