package com.jdacodes.mvicomposedemo.timer.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import com.jdacodes.mvicomposedemo.timer.util.Constants.COMPLETED
import com.jdacodes.mvicomposedemo.timer.util.Constants.DATE_FORMAT
import com.jdacodes.mvicomposedemo.timer.util.Constants.POMODORO
import com.jdacodes.mvicomposedemo.timer.util.Constants.SESSION_COLLECTION
import com.jdacodes.mvicomposedemo.timer.util.Constants.TIME_COMPLETED
import com.jdacodes.mvicomposedemo.timer.util.Constants.TIME_STARTED
import com.jdacodes.mvicomposedemo.timer.util.Constants.USER_ID
import com.jdacodes.mvicomposedemo.timer.util.Constants.ZONE_ID
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageServiceImpl : StorageService {
    private var listenerRegistration: ListenerRegistration? = null
    override fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Session) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        val query = Firebase.firestore.collection(SESSION_COLLECTION).whereEqualTo(USER_ID, userId)
        listenerRegistration = query.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }
            value?.documentChanges?.forEach {
                val wasDocumentDeleted = it.type == REMOVED
                val session = it.document.toObject(Session::class.java).copy(id = it.document.id)
                onDocumentEvent(wasDocumentDeleted, session)
            }
        }
    }

    override fun removeListener() {
        listenerRegistration?.remove()
    }

    override fun getSession(
        id: String,
        onSuccess: (Session) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Firebase.firestore
            .collection(SESSION_COLLECTION)
            .document(id)
            .get()
            .addOnFailureListener { error -> onError(error) }
            .addOnSuccessListener { result ->
                val session = result.toObject(Session::class.java)
                onSuccess(session ?: Session())
            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveSessionAsync(session: Session): String =
        suspendCancellableCoroutine { continuation ->
            val collection = Firebase.firestore.collection(SESSION_COLLECTION)

            // Create a new document reference
            val documentRef = collection.document()
            val newSessionId = documentRef.id

            // Prepare the session with the new ID
            val updatedSession = session.copy(
                id = newSessionId,
                // Ensure timeStarted is set if not provided
                timeStarted = session.timeStarted.takeIf { it.isNotEmpty() }
                    ?: ZonedDateTime.now(ZoneId.of(ZONE_ID))
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT))
            )

            // Save the updated session
            documentRef.set(updatedSession)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(newSessionId)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Unknown error saving session")
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    override suspend fun updateSessionAsync(session: Session) =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore
                .collection(SESSION_COLLECTION)
                .document(session.id)
                .update(
                    mapOf(
                        COMPLETED to session.completed,
                        POMODORO to session.pomodoro,
                        TIME_STARTED to session.timeStarted,
                        TIME_COMPLETED to session.timeCompleted,
                        // Add other fields you want to update
                        // Avoid updating user ID or other immutable fields
                    )
                )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Unknown error")
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    override suspend fun deleteSessionAsync(id: String) =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore
                .collection(SESSION_COLLECTION)
                .document(id)
                .delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Unknown error")
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getSessionsByUserIdAsync(userId: String): List<Session> =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore.collection(SESSION_COLLECTION)
                .whereEqualTo(USER_ID, userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val sessions = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Session::class.java)?.copy(id = document.id)
                    }
                    // Sort sessions by timeStarted in descending order
                    val sortedSessions = sessions.sortedByDescending { session ->
                        try {
                            ZonedDateTime.parse(
                                session.timeStarted,
                                DateTimeFormatter.ofPattern(DATE_FORMAT)
                                    .withZone(ZoneId.of(ZONE_ID))
                            )
                        } catch (e: Exception) {
                            Timber.e("Error parsing date: ${session.timeStarted}", e)
                            // Fallback to earliest possible time if parsing fails
                            ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of(ZONE_ID))
                        }
                    }
                    continuation.resume(sessions)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    override fun deleteAllSessions(userId: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(SESSION_COLLECTION)
            .whereEqualTo(USER_ID, userId)
            .get()
            .addOnFailureListener { error -> onResult(error) }
            .addOnSuccessListener { result ->
                for (document in result) document.reference.delete()
                onResult(null)
            }
    }

}