package com.jdacodes.mvicomposedemo.auth.presentation.states

data class SignUpState(
    override val email: String = "",
    override val password: String = "",
    val confirmPassword: String = "",
    val username: String = "",
    override val emailError: String? = null,
    override val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val usernameError: String? = null,
    override val isValid: Boolean = false
): AuthState.Form() {
    fun validateConfirmPassword(confirmPass: String, pass: String): String? {
        return when {
            confirmPass.isEmpty() -> "Please confirm your password"
            confirmPass != pass-> "Passwords do not match"
            else -> null
        }
    }

    fun validateUsername(username: String): String? {
        return when {
            username.isEmpty() -> "Username cannot be empty"
            username.length < 3 -> "Username must be at least 3 characters"
            else -> null
        }
    }

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
