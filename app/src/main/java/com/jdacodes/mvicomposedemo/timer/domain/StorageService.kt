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
    fun saveSession(session: Session, onResult: (Throwable?, String?) -> Unit)
    fun updateSession(session: Session, onResult: (Throwable?) -> Unit)
    fun deleteSession(id: String, onResult: (Throwable?) -> Unit)
    fun deleteAllSessions(userId: String, onResult: (Throwable?) -> Unit)

    fun getSessionsByUserId(
        userId: String,
        onSuccess: (List<Session>) -> Unit,
        onError: (Throwable) -> Unit
    )
}