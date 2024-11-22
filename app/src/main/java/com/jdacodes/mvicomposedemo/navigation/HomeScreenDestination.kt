package com.jdacodes.mvicomposedemo.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.navigation.util.ProfileRoute

enum class HomeDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
    @StringRes val contentDescription: Int,
    val route: Any
) {

    //    DASHBOARD(R.string.home, Icons.Default.Home, R.string.home),
//    USERS(R.string.users, Icons.Default.People, R.string.users),
    PROFILE(R.string.profile, Icons.Default.Person, R.string.profile, ProfileRoute)

}