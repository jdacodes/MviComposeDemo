package com.jdacodes.mvicomposedemo.di

import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.data.repository.AuthRepositoryImpl
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpNavigator
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.core.presentation.Navigator
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileNavigator
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    single {
        AuthenticationManager(
            context = androidContext(),
            webClientId = BuildConfig.WEB_CLIENT_ID_FIREBASE
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(get())
    }

}
val navigationModule = module {
    // Define NavController as a parameter
    factory<Navigator> { params ->
        LoginNavigator(params.get())
    }

    // ViewModel with parameterized injection
    viewModel { params ->
        LoginViewModel(
            authRepository = get(),
            navigator = LoginNavigator(params.get())
        )
    }

    factory<Navigator> { params ->
        SignUpNavigator(params.get())
    }

    // ViewModel with parameterized injection
    viewModel { params ->
        SignUpViewModel(
            authRepository = get(),
            navigator = SignUpNavigator(params.get())
        )
    }

    factory<Navigator> { params ->
        ForgotPasswordNavigator(params.get())
    }

    // ViewModel with parameterized injection
    viewModel { params ->
        ForgotPasswordViewModel(
            authRepository = get(),
            navigator = ForgotPasswordNavigator(params.get())
        )
    }

    factory<Navigator> { params ->
        ProfileNavigator(params.get())
    }

    // ViewModel with parameterized injection
    viewModel { params ->
        ProfileViewModel(
            authRepository = get(),
            navigator = ProfileNavigator(params.get())
        )
    }
}