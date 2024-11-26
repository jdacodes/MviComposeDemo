package com.jdacodes.mvicomposedemo.di

import androidx.navigation.NavController
import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.data.repository.AuthRepositoryImpl
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.forgot_password.ForgotPasswordViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.auth.presentation.sign_up.SignUpViewModel
import com.jdacodes.mvicomposedemo.navigation.util.Navigator
import com.jdacodes.mvicomposedemo.navigation.util.AppNavigator
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
