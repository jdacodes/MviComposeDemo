package com.jdacodes.mvicomposedemo.navigation

import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordScreen
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.navigation.util.AuthGraph
import com.jdacodes.mvicomposedemo.navigation.util.ForgotPasswordRoute
import com.jdacodes.mvicomposedemo.navigation.util.LoginRoute
import com.jdacodes.mvicomposedemo.navigation.util.SignUpRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    navigation<AuthGraph>(
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {

            val viewModel: LoginViewModel = koinViewModel { parametersOf(navController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }

        composable<SignUpRoute> {

            val viewModel: SignUpViewModel = koinViewModel { parametersOf(navController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            SignUpScreen(
                state = state,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateBack = { navController.navigateUp() },
                modifier = modifier
            )

        }

        composable<ForgotPasswordRoute> {

            val viewModel: ForgotPasswordViewModel =
                koinViewModel { parametersOf(navController) }
            val state by viewModel.state.collectAsStateWithLifecycle()
            ForgotPasswordScreen(
                state = state,
                onAction = viewModel::onAction,
                uiEffect = viewModel.uiEffect,
                onNavigateBack = { navController.navigateUp() },
                modifier = modifier
            )
        }
    }
}