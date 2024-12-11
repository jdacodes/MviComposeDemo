package com.jdacodes.mvicomposedemo.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jdacodes.mvicomposedemo.navigation.util.DashboardRoute
import com.jdacodes.mvicomposedemo.navigation.util.ProfileRoute
import com.jdacodes.mvicomposedemo.navigation.util.TimerRoute
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileScreen
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.presentation.PomodoroScreen
import com.jdacodes.mvicomposedemo.timer.presentation.TimerViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeNavGraph(
    navController: NavHostController,
    rootNavController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Any = ProfileRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
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
//            val timerViewModel: TimerViewModel = viewModel()
            val timerViewModel: TimerViewModel = koinViewModel()
            val timerState by timerViewModel.timerState.collectAsState()
            PomodoroScreen(
                timerState = timerState,
                onAction = timerViewModel::onAction,
                uiEffect = timerViewModel.uiEffect
            )
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