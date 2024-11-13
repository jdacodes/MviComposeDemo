package com.jdacodes.mvicomposedemo.auth.data

import android.app.PendingIntent
import android.content.Context
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.facebook.AccessToken
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID

class AuthenticationManager(
    private val context: Context,
    private val webClientId: String) {
    private val auth = Firebase.auth

    fun createAccountWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(AuthResponse.Success(null))
                } else {
                    trySend(AuthResponse.Error(task.exception?.message ?: ""))
                }
            }
        awaitClose()
    }

    fun loginWithEmail(email: String, password: String): Flow<AuthResponse> = callbackFlow {
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
                    trySend(AuthResponse.Error(task.exception?.message ?: ""))
                }
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

    suspend fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
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
                        auth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    trySend(AuthResponse.Success(null)).isSuccess
                                } else {
                                    trySend(AuthResponse.Error(message = task.exception?.message)).isFailure
                                }
                            }
                    } catch (e: GoogleIdTokenParsingException) {
                        trySend(AuthResponse.Error(message = e.message)).isFailure
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    trySend(AuthResponse.Error(message = "Unexpected type of credential")).isFailure
                }
            }
        } catch (e: GetCredentialException) {
            trySend(AuthResponse.Error(message = e.message)).isFailure
        }
        awaitClose()
    }

    fun createIntent(onSuccess: (IntentSenderRequest) -> Unit) {
        // Creating the request
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(webClientId)
            .build()

        // Gathering the SignInIntent
        Identity.getSignInClient(context)
            .getSignInIntent(request)
            .addOnSuccessListener { result: PendingIntent ->
                // Handling the response
                try {
                    onSuccess(
                        IntentSenderRequest.Builder(
                            result.intentSender
                        ).build()
                    )

                } catch (e: IntentSender.SendIntentException) {
                    Log.e("Google Sign-in", "failed")
                }
            }
            .addOnFailureListener { exception: Exception? ->
                Log.e("Google Sign-in failed", exception?.message ?: "failed")
            }
    }

    fun signInWithFacebook(token: AccessToken) = callbackFlow {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
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
                    // If sign in fails, display a message to the user.
                    trySend(AuthResponse.Error(task.exception?.message ?: ""))
                }
            }
        awaitClose()
    }
}

interface AuthResponse {
    data class Success(val user: User?) : AuthResponse
    data class Error(val message: String?) : AuthResponse
}