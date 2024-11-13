package com.jdacodes.mvicomposedemo.auth.presentation.states

data class LoginState(
     override val email: String = "",
     override val password: String = "",
     override val emailError: String? = null,
     override val passwordError: String? = null,
     override val isValid: Boolean = false
): AuthState.Form() {
     override fun validateEmail(email: String): String? {
          return when {
               email.isEmpty() -> "Email cannot be empty"
               !email.contains("@") -> "Invalid email format"
               else -> null
          }
     }

      override fun validatePassword(password: String): String? {
          return when {
               password.isEmpty() -> "Password cannot be empty"
               password.length < 8 -> "Password must be at least 8 characters"
               else -> null
          }
     }
}