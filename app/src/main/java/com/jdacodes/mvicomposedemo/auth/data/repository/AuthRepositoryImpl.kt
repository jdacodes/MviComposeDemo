package com.jdacodes.mvicomposedemo.auth.data.repository

import android.content.Context
import com.facebook.AccessToken
import com.jdacodes.mvicomposedemo.auth.data.AuthError
import com.jdacodes.mvicomposedemo.auth.data.AuthResponse
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(private val authenticationManager: AuthenticationManager) :
    AuthRepository {
    override suspend fun login(context: Context, email: String, password: String): User {
        return try {
            authenticationManager.loginWithEmail(email, password)
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            User(
                                id = response.data.id ?: "",
                                email = response.data.email ?: "",
                                username = response.data.username ?: "",
                            )
                        }

                        is AuthResponse.Error -> {
                            when (response.error) {
                                AuthError.InvalidCredentials -> {
                                    Timber.e("Invalid credentials")
                                    throw InvalidCredentialsException("Invalid credentials")
                                }

                                AuthError.InvalidEmail -> {
                                    Timber.e("Invalid email address")
                                    throw InvalidEmailException("Invalid email address")
                                }

                                AuthError.Unknown -> {
                                    Timber.e("Unknown authentication error")
                                    throw UnknownAuthException("Unknown authentication error")
                                }

                                else -> {
                                    throw Exception(response.error.toString())
                                }
                            }

                        }

                        else -> {
                            Timber.e("Invalid credentials")
                            throw Exception("Invalid credentials")
                        }
                    }
                }
                .first() // Convert Flow to single value}
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun signUp(email: String, password: String, username: String): User? {
        return try {
            authenticationManager.createAccountWithEmail(email, password)
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> null  // Return null on success
                        is AuthResponse.Error -> {
                            Timber.e("Sign up failed with error: ${response.error}")
                            throw Exception(response.error.toString())
                        }

                        else -> throw Exception("Unknown error occurred")
                    }
                }
                .first()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInWithGoogle(): User {
        return try {
            authenticationManager.signInWithGoogle()
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            response.data
                        }

                        is AuthResponse.Error -> {
                            // Handle AuthError types
                            when (response.error) {
                                AuthError.InvalidCredentials -> {
                                    Timber.e("Invalid credentials for Google sign-in")
                                    throw InvalidCredentialsException(
                                        response.error.toString()
                                    )
                                }

                                AuthError.GoogleIdTokenParsing -> {
                                    Timber.e("Google ID token parsing failed")
                                    throw GoogleIdTokenParsingException(
                                        response.error.toString()
                                    )
                                }
                                // Handle other AuthError types as needed
                                else -> {
                                    Timber.e("Sign-in failed with error: ${response.error}")
                                    throw Exception("Sign-in failed with error: ${response.error}")
                                }
                            }
                        }
                    }
                }
                .first() // Convert Flow to single value}
        } catch (e: Exception) {
            // Handle exceptions during flow collection or unexpected exceptions
            when (e) {
                is CancellationException -> throw e // Re-throw cancellation exceptions
                else -> {
                    Timber.e(e, "Error during Google sign-in") // Log the error
                    throw GoogleIdAuthenticationException("Failed to sign in with Google") // Throw a custom exception
                }
            }
        }
    }

    override suspend fun signInWithFacebook(token: AccessToken): User {
        return try {
            authenticationManager.signInWithFacebook(token)
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            User(
                                id = response.data.id ?: "",
                                email = response.data.email ?: "",
                                username = response.data.username ?: "",
                            )
                        }

                        is AuthResponse.Error -> {
                            when (response.error) {
                                AuthError.InvalidCredentials -> {
                                    Timber.e("Invalid credentials for password reset")
                                    throw InvalidCredentialsException("Invalid credentials")
                                }

                                AuthError.InvalidEmail -> {
                                    Timber.e("Invalid email address")
                                    throw InvalidEmailException("Invalid email address")
                                }

                                AuthError.AccountAlreadyExists -> {
                                    Timber.e("Account already exists")
                                    throw AccountAlreadyExistsException("Account already exists")
                                }

                                AuthError.Unknown -> {
                                    Timber.e("Unknown authentication error")
                                    throw UnknownAuthException("Unknown authentication error")
                                }

                                AuthError.GoogleIdTokenParsing -> {
                                    Timber.e("Google ID token parsing failed")
                                    throw GoogleIdTokenParsingException("Google ID token parsing failed")
                                }

                                AuthError.LoginWithEmail -> TODO()
                            }
                        }
                    }
                }
                .first()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun forgotPassword(email: String): Boolean {
        return try {
            val response = authenticationManager.sendPasswordResetEmail(email)
            when (response) {
                is AuthResponse.Success -> {
                    // Password reset email sent successfully
                    Timber.d("Password reset email sent successfully to $email")
                    true
                }

                is AuthResponse.Error -> {
                    when (response.error) {
                        AuthError.InvalidEmail -> {
                            Timber.e("Invalid email address: $email")
                            throw InvalidEmailException("Invalid email address")
                        }

                        AuthError.InvalidCredentials -> {
                            Timber.e("Invalid credentials for password reset")
                            throw InvalidCredentialsException("Invalid credentials")
                        }

                        else -> {
                            Timber.e("Unexpected error during password reset")
                            throw UnknownAuthException("Unexpected error during password reset")
                        }
                    }

                }

            }
        } catch (e: Exception) {
            // Handle unexpected exceptions
            Timber.e(e, "Unexpected error during password reset")
            if (e is CancellationException) throw e else throw UnknownAuthException(
                "Unexpected error",
                e
            )
        }

    }
}

class InvalidEmailException(message: String) : Exception(message)
class InvalidCredentialsException(message: String) : Exception(message)
class AccountAlreadyExistsException(message: String) : Exception(message)
class GoogleIdTokenParsingException(message: String) : Exception(message)
class GoogleIdAuthenticationException(message: String) : Exception(message)
class UnknownAuthException(message: String, cause: Throwable? = null) : Exception(message, cause)