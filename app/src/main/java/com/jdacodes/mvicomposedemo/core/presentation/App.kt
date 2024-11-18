package com.jdacodes.mvicomposedemo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.data.repository.AuthRepositoryImpl
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordScreen
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModelFactory
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModelFactory
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpScreen
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModelFactory
import kotlinx.serialization.Serializable

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
    val context = LocalContext.current
    val authenticationManager = remember {
        AuthenticationManager(
            context,
            BuildConfig.WEB_CLIENT_ID_FIREBASE
        )
    }


    val rootNavController = rememberNavController()
    val authRepository = remember { AuthRepositoryImpl(authenticationManager) }
    NavHost(
        navController = rootNavController,
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            val context = LocalContext.current
            val navigator = LoginNavigator(rootNavController)
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModelFactory(authRepository, navigator)
            )
            val state by viewModel.state.collectAsStateWithLifecycle()
            LoginScreen(
                state = state,
                uiEffect = viewModel.uiEffect,
                onAction = viewModel::onAction,
                modifier = modifier
            )
        }

        composable<SignUpRoute> {
            val context = LocalContext.current
            val navigator = SignUpNavigator(rootNavController)
            val viewModel: SignUpViewModel = viewModel(
                factory = SignUpViewModelFactory(authRepository, navigator)
            )
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
            val context = LocalContext.current
            val navigator = ForgotPasswordNavigator(rootNavController)
            val viewModel: ForgotPasswordViewModel = viewModel(
                factory = ForgotPasswordViewModelFactory(authRepository, navigator)
            )
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
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Home Screen")
            }
        }
    }


}