package com.jdacodes.mvicomposedemo.timer.data.local.repository

import com.jdacodes.mvicomposedemo.timer.data.local.StorageDao
import com.jdacodes.mvicomposedemo.timer.data.local.entity.SessionEntity
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class StorageRepositoryImpl(
    private val storageService: StorageService,
    private val storageDao: StorageDao
):StorageRepository {
    override suspend fun addListener(
        userId: String,
        onDocumentEvent: (Boolean, Session) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        Timber.d("Adding listener for user ID: $userId")
        storageService.addListener(userId, onDocumentEvent, onError)
    }

    override suspend fun removeListener() {
        Timber.d("Removing listener")
        storageService.removeListener()
    }

    override suspend fun saveSession(session: Session) {
        // Save to Firestore and local database
        storageService.saveSession(session) { error, newSessionId ->
            if (error == null && newSessionId != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Saving session to local database: $session")
                    storageDao.saveSession(SessionEntity.fromDomain(session.copy(id = newSessionId)))
                }
            }
        }
    }

    override suspend fun updateSession(session: Session) {
        storageService.updateSession(session) { error ->
            if (error == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Updating session in local database: $session")
                    storageDao.updateSession(SessionEntity.fromDomain(session))
                }
            }
        }
    }

    override suspend fun deleteSession(sessionId: String) {
        storageService.deleteSession(sessionId) { error ->
            if (error == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    Timber.d("Deleting session from local database: $sessionId")
                    storageDao.deleteSession(sessionId)
                }
            }
        }
    }

    override suspend fun getSessionsByUserId(userId: String): List<Session> {
        Timber.d("Getting sessions for user ID: $userId")
        return storageDao.getSessionsByUserId(userId).map { it.toDomain() }
    }

    override suspend fun getSession(sessionId: String): Session? {
        Timber.d("Getting session with ID: $sessionId")
        return storageDao.getSession(sessionId)?.toDomain()
    }
}