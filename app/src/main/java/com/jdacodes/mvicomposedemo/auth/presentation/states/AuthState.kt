package com.jdacodes.mvicomposedemo.auth.presentation.states

import com.jdacodes.mvicomposedemo.auth.data.AuthResponse
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

sealed class AuthState {
    sealed class Form : AuthState() {
        abstract fun validateEmail(email:String): String?
        abstract fun validatePassword(password: String): String?

        abstract val email: String
        abstract val password: String
        abstract val emailError: String?
        abstract val passwordError: String?
        abstract val isValid: Boolean
    }
    data object Loading : AuthState()
    data class Success(val user: User?) : AuthState()
    data class Error(val message: String) : AuthState()
    data object Sent : AuthState()
}