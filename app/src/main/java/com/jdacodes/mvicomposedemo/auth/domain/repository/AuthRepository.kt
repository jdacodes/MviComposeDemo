package com.jdacodes.mvicomposedemo.auth.domain.repository

import android.content.Context
import com.facebook.AccessToken
import com.jdacodes.mvicomposedemo.auth.data.AuthResponse
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(context: Context, email: String, password: String): User
    suspend fun signUp(email: String, password: String, username: String): User?
    suspend fun signInWithGoogle(): User
    suspend fun signInWithFacebook(token: AccessToken): User
    suspend fun forgotPassword(email:String): Boolean
    suspend fun getCurrentUser(): User?
    suspend fun signOutUser(): Boolean
    suspend fun reloadUser(): Boolean
}