package com.jdacodes.mvicomposedemo.navigation.util

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import kotlinx.serialization.Serializable

@Serializable
data object AuthGraph

@Serializable
data object HomeGraph

//Auth graph screens
@Serializable
data object LoginRoute

@Serializable
data object SignUpRoute

@Serializable
data object ForgotPasswordRoute

//Home graph screens
@Serializable
data object ProfileRoute

@Serializable
data object TimerRoute

@Serializable
data object DashboardRoute

fun Navigator.navigateTo(route: Any, options: NavOptionsBuilder.() -> Unit = {}) {
    navigateTo(route, options)
}
//Navigation extensions for Auth graph
fun Navigator.navigateLoginToSignUpRoute() {
    navigateTo(SignUpRoute) {
        popUpTo(LoginRoute) { inclusive = false }
    }
}

fun Navigator.navigateLoginToForgotPasswordRoute() {
    navigateTo(ForgotPasswordRoute) {
        popUpTo(LoginRoute) { inclusive = false }
    }
}

fun Navigator.navigateLoginToHomeGraph() {
    navigateTo(HomeGraph) {
        popUpTo(LoginRoute) { inclusive = true }
    }
}

fun Navigator.navigateForgotPasswordToLoginRoute() {
    navigateTo(LoginRoute) {
        popUpTo(ForgotPasswordRoute) { inclusive = true }
    }
}

fun Navigator.navigateSignUpToLoginRoute() {
    navigateTo(LoginRoute) {
        popUpTo(SignUpRoute) { inclusive = true }
    }
}

fun Navigator.navigateProfileToAuthGraph(navController: NavHostController) {
//    navigateTo(AuthGraph) {
//        popUpTo(0) {}
//    }
    navController.navigate(AuthGraph) {
        popUpTo(0) {}
    }
}



