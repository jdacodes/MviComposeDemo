package com.jdacodes.mvicomposedemo.auth.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.facebook.AccessToken
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.security.MessageDigest
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

class AuthenticationManager(
    private val context: Context,
    private val webClientId: String
) {
    private val auth = Firebase.auth

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse<Unit>> =
        callbackFlow {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                trySend(AuthResponse.Success(Unit))
            } catch (e: Exception) {
                val authError = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
                    is FirebaseAuthInvalidUserException -> AuthError.InvalidEmail
                    is FirebaseAuthUserCollisionException -> AuthError.AccountAlreadyExists
                    is FirebaseAuthEmailException -> AuthError.InvalidEmail
                    else -> AuthError.Unknown // Or AuthError.Unknown(e.message) if you want to include the message
                }
                trySend(AuthResponse.Error(authError))
            }
            awaitClose()
        }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse<User>> = callbackFlow {
        try {

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        trySend(
                            AuthResponse.Success(
                                User(
                                    id = task.result.user?.uid ?: "",
                                    email = task.result.user?.email ?: "",
                                    username = task.result.user?.displayName ?: "",
                                )
                            )
                        )
                    } else {
                        trySend(AuthResponse.Error(AuthError.InvalidCredentials))
                    }
                }
        } catch (e: Exception) {
            val authError = when (e) {
                is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
                is FirebaseAuthInvalidUserException -> AuthError.InvalidEmail // Assuming this maps to invalid email
                // Add other specific exception mappings as needed
                else -> AuthError.Unknown
            }
            trySend(AuthResponse.Error(authError))
        }
        awaitClose()
    }


    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)

        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }

    suspend fun signInWithGoogle(): Flow<AuthResponse<User>> = callbackFlow {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            // Pass web client ID
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .setNonce(createNonce())
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )

            // Handle the successfully returned credential.
            val credential = result.credential
            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        // Sign in to firebase
                        val firebaseCredential =
                            GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                        val user = auth.signInWithCredential(firebaseCredential).await().user
                        trySend(AuthResponse.Success(firebaseUserToUser(user))).isSuccess
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.e("Google ID token parsing failed")
                        trySend(AuthResponse.Error(AuthError.GoogleIdTokenParsing)).isFailure
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Timber.e("Invalid credentials for Google sign-in")
                        AuthResponse.Error(AuthError.InvalidCredentials)
                    }

                } else {
                    // Catch any unrecognized custom credential type here.
                    Timber.e("Unknown credential type: ${credential.type}")
                    trySend(AuthResponse.Error(AuthError.InvalidCredentials)).isFailure
                }
            }
        } catch (e: GetCredentialException) {
            trySend(AuthResponse.Error(AuthError.Unknown)).isFailure
        }
        awaitClose()
    }

    fun signInWithFacebook(token: AccessToken) = callbackFlow {
        val credential = FacebookAuthProvider.getCredential(token.token)
        try {
            val authResult = auth.signInWithCredential(credential).await()
            trySend(AuthResponse.Success(firebaseUserToUser(authResult.user)))
        } catch (e: Exception) {
            val authError = when (e) {
                is FirebaseAuthInvalidCredentialsException -> AuthError.InvalidCredentials
                is FirebaseAuthInvalidUserException -> AuthError.InvalidEmail // Assuming this maps to invalid email
                is FirebaseAuthUserCollisionException -> AuthError.AccountAlreadyExists
                // Add other specific exception mappings as needed
                else -> AuthError.Unknown
            }
            trySend(AuthResponse.Error(authError))
        }
        awaitClose()
    }

    suspend fun sendPasswordResetEmail(email: String): AuthResponse<Unit> = try {
        if (!coroutineContext.isActive) {
            throw CancellationException("Password reset operation cancelled")
        }
        auth.sendPasswordResetEmail(email).await()
        AuthResponse.Success(Unit)
    } catch (e: FirebaseAuthInvalidUserException) {
        Timber.e(e, "Invalid user email: %s", email)
        AuthResponse.Error(AuthError.InvalidEmail)
    } catch (e: FirebaseAuthInvalidCredentialsException) {
        Timber.e(e, "Invalid credentials for password reset")
        AuthResponse.Error(AuthError.InvalidCredentials)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Unexpected error during password reset")
        AuthResponse.Error(AuthError.Unknown)
    }

    fun signOutUser(): AuthResponse<Unit> =
        try {
            auth.signOut()
            AuthResponse.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error signing out")
            AuthResponse.Error(AuthError.Unknown)
        }

    fun getCurrentUser(): AuthResponse<User?> {
        return try {
            val firebaseUser = auth.currentUser
            val user = firebaseUser?.let { firebaseUserToUser(it) }
            return if (user != null) {
                Timber.d("Current user: $user")
                AuthResponse.Success(user)
            } else {
                Timber.d("No current user")
                AuthResponse.Error(AuthError.Unknown)
            }
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error during get current user")
            AuthResponse.Error(AuthError.Unknown)
        }

    }

    fun reloadFirebaseUser(): AuthResponse<Boolean> {
        return try {
            auth.currentUser?.reload()
            AuthResponse.Success(true)
        } catch (e: Exception) {
            AuthResponse.Error(AuthError.Unknown)
        }
    }
}


sealed class AuthResponse<out T> {
    data class Success<out T>(val data: T) : AuthResponse<T>()
    data class Error(val error: AuthError) : AuthResponse<Nothing>()
}

enum class AuthError {
    InvalidEmail,
    InvalidCredentials,
    Unknown,
    AccountAlreadyExists,
    GoogleIdTokenParsing,
}

fun firebaseUserToUser(firebaseUser: FirebaseUser?): User {
    return User(
        id = firebaseUser?.uid ?: "",
        email = firebaseUser?.email ?: "",
        username = firebaseUser?.displayName ?: ""
    )
}