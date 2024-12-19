package com.jdacodes.mvicomposedemo.timer.data.local.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.jdacodes.mvicomposedemo.timer.data.local.StorageDao
import com.jdacodes.mvicomposedemo.timer.data.local.entity.SessionEntity
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import com.jdacodes.mvicomposedemo.timer.util.Constants.DATE_FORMAT
import com.jdacodes.mvicomposedemo.timer.util.Constants.ZONE_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class StorageRepositoryImpl(
    private val storageService: StorageService,
    private val storageDao: StorageDao
) : StorageRepository {
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


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun saveSession(session: Session): String = withContext(Dispatchers.IO) {
        try {
            val newSessionId = when {
                // Case 1: Session already has an existing ID
                session.id.isNotEmpty() -> {
                    // Update the existing session
                    storageService.updateSessionAsync(session)
                    session.id
                }

                // Case 2: No existing ID, but session has other identifying information
                session.timeStarted.isNotEmpty() -> {
                    // Save as a new session
                    storageService.saveSessionAsync(session)
                }

                // Case 3: Completely new session with no identifying information
                else -> {
                    // Generate a new session with default values
                    storageService.saveSessionAsync(
                        session.copy(
                            timeStarted = ZonedDateTime.now(ZoneId.of(ZONE_ID))
                                .format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                        )
                    )
                }
            }

            // Save to local database
            val sessionToSave = session.copy(id = newSessionId)
            storageDao.saveSession(SessionEntity.fromDomain(sessionToSave))

            newSessionId
        } catch (e: Exception) {
            Timber.e("StorageRepositoryImpl: Error saving session $e")
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

    override suspend fun getSessionsByUserId(userId: String): List<Session> =
        withContext(Dispatchers.IO) {
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