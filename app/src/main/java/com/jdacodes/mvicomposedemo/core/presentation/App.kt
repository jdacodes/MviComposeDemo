package com.jdacodes.mvicomposedemo.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordScreen
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileScreen
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object LoginRoute

@Serializable
data object SignUpRoute

@Serializable
data object ForgotPasswordRoute

@Serializable
data object HomeRoute

@Composable
fun App(
    modifier: Modifier = Modifier
) {
    val rootNavController = rememberNavController()
    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {

            val viewModel: LoginViewModel = koinViewModel { parametersOf(rootNavController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }

        composable<SignUpRoute> {

            val viewModel: SignUpViewModel = koinViewModel { parametersOf(rootNavController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            SignUpScreen(
                state = state,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateBack = { rootNavController.navigateUp() },
                modifier = modifier
            )

        }

        composable<ForgotPasswordRoute> {

            val viewModel: ForgotPasswordViewModel =
                koinViewModel { parametersOf(rootNavController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            ForgotPasswordScreen(
                state = state,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateBack = { rootNavController.navigateUp() },
                modifier = modifier
            )
        }

        composable<HomeRoute> {

            val viewModel: ProfileViewModel = koinViewModel { parametersOf(rootNavController) }
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