package com.jdacodes.mvicomposedemo.di

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.splash.presentation.SplashViewModel
import com.jdacodes.mvicomposedemo.navigation.util.AppNavigator
import com.jdacodes.mvicomposedemo.navigation.util.Navigator
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.presentation.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigationModule = module {
    // Define NavController as a parameter
    factory<Navigator> { params ->
        AppNavigator(params.get())
    }

    // ViewModel with parameterized injection
    viewModel { (navController: NavController) ->
        LoginViewModel(
            authRepository = get(),
            navigator = AppNavigator(navController)
        )
    }

    // ViewModel with parameterized injection
    viewModel { params ->
        SignUpViewModel(
            authRepository = get(),
            navigator = AppNavigator(params.get())
        )
    }

    // ViewModel with parameterized injection
    viewModel { (navController: NavController) ->
        ForgotPasswordViewModel(
            authRepository = get(),
            navigator = AppNavigator(navController)
        )
    }

    // ViewModel with parameterized injection
    viewModel { (navController: NavController) ->
        ProfileViewModel(
            authRepository = get(),
            navigator = AppNavigator(navController)
        )
    }

    viewModel {(savedStateHandle: SavedStateHandle) ->
        SplashViewModel(
            authRepository = get(),
            savedStateHandle = savedStateHandle
        )
    }

    viewModel {
        TimerViewModel(
           storageRepository = get(),
            authRepository = get()
        )

    }
}