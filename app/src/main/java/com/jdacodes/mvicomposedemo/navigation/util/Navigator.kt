package com.jdacodes.mvicomposedemo.navigation.util

import androidx.navigation.NavOptionsBuilder

interface Navigator {
//    fun navigateToSignUp()
//    fun navigateToForgotPassword()
//    fun navigateToHome()
//    fun navigateToLogin()
fun navigateTo(route: Any, options: NavOptionsBuilder.() -> Unit = {})
}