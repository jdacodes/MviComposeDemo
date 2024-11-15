package com.jdacodes.mvicomposedemo.auth.data.repository

import android.content.Context
import com.facebook.AccessToken
import com.jdacodes.mvicomposedemo.auth.data.AuthResponse
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(private val authenticationManager: AuthenticationManager) :
    AuthRepository {
    override suspend fun login(context: Context, email: String, password: String): User {
        return try {
            authenticationManager.loginWithEmail(email, password)
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            User(
                                id = response.user?.id ?: "",
                                email = response.user?.email ?: "",
                                username = response.user?.username ?: "",
                            )
                        }

                        is AuthResponse.Error -> throw Exception(response.message)
                        else -> throw Exception("Invalid credentials")
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
                        is AuthResponse.Error -> throw Exception(response.message)
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
                            User(
                                id = response.user?.id ?: "",
                                email = response.user?.email ?: "",
                                username = response.user?.username ?: "",
                            )
                        }

                        is AuthResponse.Error -> throw Exception(response.message)
                        else -> throw Exception("Invalid credentials")
                    }
                }
                .first() // Convert Flow to single value}
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInWithFacebook(token: AccessToken): User {
        return try {
            authenticationManager.signInWithFacebook(token)
                .map { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            User(
                                id = response.user?.id ?: "",
                                email = response.user?.email ?: "",
                                username = response.user?.username ?: "",
                            )
                        }

                        is AuthResponse.Error -> throw Exception(response.message)
                        else -> throw Exception("Invalid credentials")
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
                is AuthResponse.Success ->  true
                is AuthResponse.Error -> false
                else -> false
            }
        } catch (e: Exception) {
            throw e
        }

    }
}