package com.jdacodes.mvicomposedemo.timer.data.local.repository

import com.jdacodes.mvicomposedemo.timer.data.local.StorageDao
import com.jdacodes.mvicomposedemo.timer.data.local.entity.SessionEntity
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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

override suspend fun saveSession(session: Session): String = withContext(Dispatchers.IO) {
    try {
        // First, save to Firestore
        val newSessionId = storageService.saveSessionAsync(session)

        // Then save to local database
        val sessionToSave = session.copy(id = newSessionId)
        storageDao.saveSession(SessionEntity.fromDomain(sessionToSave))
        newSessionId
    } catch (e: Exception) {
        // Handle any errors that might occur during the process
        Timber.e("StorageRepositoryImpl:Error saving session $e")
        throw e
    }
}

    override suspend fun updateSession(session: Session) = withContext(Dispatchers.IO) {
        try {
            // First, update in Firestore
            storageService.updateSessionAsync(session)

            // Then update in local database
            storageDao.updateSession(SessionEntity.fromDomain(session))
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSession(sessionId: String) = withContext(Dispatchers.IO) {
        try {
            // First, delete from Firestore
            storageService.deleteSessionAsync(sessionId)

            // Then delete from local database
            storageDao.deleteSession(sessionId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSessionsByUserId(userId: String): List<Session> = withContext(Dispatchers.IO) {
        try {
            // First, try to fetch from remote service
            val remoteSessions = storageService.getSessionsByUserIdAsync(userId)

            // If remote fetch is successful, update local database
            if (remoteSessions.isNotEmpty()) {
                // Convert and save to local database
                val sessionEntities = remoteSessions.map { SessionEntity.fromDomain(it) }
                storageDao.deleteAllSessions() // Optional: clear existing local sessions
                storageDao.insertSessions(sessionEntities)
            }

            // Fetch and return from local database (which now includes remote data)
            storageDao.getSessionsByUserId(userId).map { it.toDomain() }
        } catch (e: Exception) {
            Timber.e("Error fetching sessions for user $userId: ${e.message}")

            // Fallback to local database if remote fetch fails
            storageDao.getSessionsByUserId(userId).map { it.toDomain() }
        }
    }

    override suspend fun getSession(sessionId: String): Session? {
        Timber.d("Getting session with ID: $sessionId")
        return storageDao.getSession(sessionId)?.toDomain()
    }
}