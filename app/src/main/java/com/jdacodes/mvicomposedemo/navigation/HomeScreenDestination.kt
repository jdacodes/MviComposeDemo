package com.jdacodes.mvicomposedemo.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.navigation.util.DashboardRoute
import com.jdacodes.mvicomposedemo.navigation.util.ProfileRoute
import com.jdacodes.mvicomposedemo.navigation.util.TimerRoute

enum class HomeDestinations(
    @StringRes val label: Int,
    val iconUnselected: ImageVector,
    val iconSelected: ImageVector,
    @StringRes val contentDescription: Int,
    val route: Any
) {
    DASHBOARD(
        R.string.dashboard,
        Icons.Outlined.Dashboard,
        Icons.Default.Dashboard,
        R.string.dashboard,
        DashboardRoute
    ),
    PROFILE(
        R.string.profile,
        Icons.Outlined.Person,
        Icons.Default.Person,
        R.string.profile,
        ProfileRoute
    ),
    TIMER(
        R.string.pomodoro,
        Icons.Outlined.Timer,
        Icons.Default.Timer,
        R.string.pomodoro,
        TimerRoute
    )

}