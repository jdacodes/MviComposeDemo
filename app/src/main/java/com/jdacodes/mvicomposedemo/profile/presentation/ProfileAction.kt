package com.jdacodes.mvicomposedemo.profile.presentation

import androidx.navigation.NavHostController

sealed class ProfileAction {
    data object DisplayUserDetails : ProfileAction()
    data object SignOut : ProfileAction()
    data class NavigateToAuth(val navController: NavHostController): ProfileAction()
}