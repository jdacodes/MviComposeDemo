package com.jdacodes.mvicomposedemo.timer.data.remote

import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import kotlinx.coroutines.suspendCancellableCoroutine
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
//                val session = result.toObject<Session>()?.copy(id = result.id)
                val session = result.toObject(Session::class.java)
                onSuccess(session ?: Session())
            }
    }

    override suspend fun saveSessionAsync(session: Session): String =
        suspendCancellableCoroutine { continuation ->
            val collection = Firebase.firestore.collection(SESSION_COLLECTION)
            val documentRef =
                if (session.id.isNotEmpty()) collection.document(session.id) else collection.document()
            val newSessionId = documentRef.id
            val updatedSession = session.copy(id = newSessionId)

            documentRef.set(updatedSession)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(newSessionId)
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

    override suspend fun updateSessionAsync(session: Session) =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore
                .collection(SESSION_COLLECTION)
                .document(session.id)
//                .set(session)
                .update(
                    mapOf(
                        COMPLETED to session.completed,
                        POMODORO to session.pomodoro,
                        TIME_STARTED to session.timeStarted,
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

    override suspend fun getSessionsByUserIdAsync(userId: String): List<Session> =
        suspendCancellableCoroutine { continuation ->
            Firebase.firestore.collection(SESSION_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val sessions = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Session::class.java)?.copy(id = document.id)
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

    companion object {
        private const val SESSION_COLLECTION = "Session"
        private const val USER_ID = "userId"
        private const val COMPLETED = "completed"
        private const val POMODORO = "pomodoro"
        private const val TIME_STARTED = "timeStarted"
    }
}