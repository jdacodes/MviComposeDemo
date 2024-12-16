package com.jdacodes.mvicomposedemo.timer.domain

import com.jdacodes.mvicomposedemo.timer.domain.model.Session

interface StorageService {
    fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Session) -> Unit,
        onError: (Throwable) -> Unit
    )

    fun removeListener()
    fun getSession(id: String, onSuccess: (Session) -> Unit, onError: (Throwable) -> Unit)
    fun deleteAllSessions(userId: String, onResult: (Throwable?) -> Unit)
    suspend fun saveSessionAsync(session: Session): String
    suspend fun updateSessionAsync(session: Session)
    suspend fun deleteSessionAsync(id: String)
    suspend fun getSessionsByUserIdAsync(userId: String): List<Session>
}