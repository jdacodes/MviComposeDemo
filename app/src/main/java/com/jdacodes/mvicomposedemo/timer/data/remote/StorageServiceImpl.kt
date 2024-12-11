package com.jdacodes.mvicomposedemo.timer.data.remote

import com.google.firebase.firestore.DocumentChange.Type.REMOVED
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session

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
                val session = result.toObject<Session>()?.copy(id = result.id)
                onSuccess(session ?: Session())
            }
    }

    override fun saveSession(session: Session, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(SESSION_COLLECTION)
            .add(session)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun updateSession(session: Session, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(SESSION_COLLECTION)
            .document(session.id)
            .set(session)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun deleteSession(id: String, onResult: (Throwable?) -> Unit) {
        Firebase.firestore
            .collection(SESSION_COLLECTION)
            .document(id)
            .delete()
            .addOnCompleteListener { onResult(it.exception) }
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
    }
}