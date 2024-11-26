package com.jdacodes.mvicomposedemo.navigation.util

import androidx.navigation.NavOptionsBuilder

interface Navigator {
    fun navigateTo(route: Any, options: NavOptionsBuilder.() -> Unit = {})
}