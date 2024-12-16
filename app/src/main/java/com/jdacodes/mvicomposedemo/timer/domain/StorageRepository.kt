package com.jdacodes.mvicomposedemo.timer.domain

import com.jdacodes.mvicomposedemo.timer.domain.model.Session

interface StorageRepository {
    suspend fun addListener(userId: String, onDocumentEvent: (Boolean, Session) -> Unit, onError: (Throwable) -> Unit)
    suspend fun removeListener()
    suspend fun saveSession(session: Session): String
    suspend fun updateSession(session: Session)
    suspend fun deleteSession(sessionId: String)
    suspend fun getSessionsByUserId(userId: String): List<Session>
    suspend fun getSession(sessionId: String): Session?
}
