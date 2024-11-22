package com.jdacodes.mvicomposedemo.navigation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileScreen
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    logout: () -> Unit,
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
                modifier = modifier,
                uiEffect = viewModel.effect,
                onAction = viewModel::onAction
            )
        }
    }
}