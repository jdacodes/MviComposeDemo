package com.jdacodes.mvicomposedemo.navigation.util

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
