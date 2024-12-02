package com.jdacodes.mvicomposedemo.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jdacodes.mvicomposedemo.auth.presentation.timer.presentation.TimerScreen
import com.jdacodes.mvicomposedemo.navigation.util.DashboardRoute
import com.jdacodes.mvicomposedemo.navigation.util.ProfileRoute
import com.jdacodes.mvicomposedemo.navigation.util.TimerRoute
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileScreen
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = ProfileRoute
    ) {
        composable<ProfileRoute> {

            val viewModel: ProfileViewModel = koinViewModel { parametersOf(navController) }
            val state by viewModel.state.collectAsStateWithLifecycle()

            ProfileScreen(
                state = state,
                rootNavController = rootNavController,
                modifier = modifier,
                uiEffect = viewModel.effect,
                onAction = viewModel::onAction
            )
        }

        composable<TimerRoute> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                TimerScreen()
            }
        }

        composable<DashboardRoute> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text("Dashboard")
            }
        }
    }
}