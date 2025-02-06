package com.jdacodes.mvicomposedemo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jdacodes.mvicomposedemo.auth.data.local.AppDatabase
import com.jdacodes.mvicomposedemo.timer.data.local.StorageDao
import com.jdacodes.mvicomposedemo.timer.data.local.entity.SessionEntity
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.jdacodes.mvicomposedemo.timer.util.Constants.DATE_FORMAT
import com.jdacodes.mvicomposedemo.timer.util.Constants.ZONE_ID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionTests {
    private lateinit var appDatabase: AppDatabase
    private lateinit var storageDao: StorageDao

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        storageDao = appDatabase.storageDao()
    }

    @Test
    fun savingSessionEntityAndVerifyingStorage() = runBlocking {
        //Arrange dummy Entity
        val currentTime = ZonedDateTime.now(ZoneId.of(ZONE_ID))
        val formattedTime = currentTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT))

        val session = SessionEntity(
            id = "test_id",
            userId = "test_user",
            pomodoro = 0,
            completed = false,
            timeStarted = formattedTime
        )
       //Assert list of Session Entity is empty initially
        assertEquals(0, storageDao.getSessionsByUserId("test_user").size)
        //Call action to save Session entity
        storageDao.saveSession(session)
        //Call function for list of Sessions entity and Assert validity of our Session entity
        val sessions = storageDao.getSessionsByUserId("test_user")
        assertTrue(sessions.isNotEmpty())
        assertEquals(1, sessions.size)
        assert(sessions.contains(session))
        assertEquals("test_id", sessions[0].id)
    }



    @After
    fun tearDown() {
        appDatabase.close()
    }
}