package com.jdacodes.mvicomposedemo.navigation.util

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

class AppNavigator(
    private val navController: NavController
): Navigator {
    override fun navigateTo(route: Any, options: NavOptionsBuilder.() -> Unit) {
        navController.navigate(route, options)
    }
}