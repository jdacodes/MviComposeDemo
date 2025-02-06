package com.jdacodes.mvicomposedemo

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jdacodes.mvicomposedemo.auth.data.local.AppDatabase
import com.jdacodes.mvicomposedemo.auth.data.local.UserDao
import com.jdacodes.mvicomposedemo.auth.data.local.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserTests {
    private lateinit var appDatabase: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        userDao = appDatabase.userDao()
    }

    @Test
    fun insertUserEntityAndVerifyStorage() = runBlocking {
        val user = UserEntity(
            id = "sea",
            email = "deana.franklin@example.com",
            displayName = "Lincoln Franks",
            isLoggedIn = true,
            photoUrl = "https://www.database.com/img.jpg"
        )

        assertEquals(null, userDao.getLoggedInUser())
        userDao.insertUser(user)
        assert(userDao.getLoggedInUser() != null)
        assertEquals(user, userDao.getLoggedInUser())
        assertEquals("sea", userDao.getLoggedInUser()?.id)

    }

    @Test
    fun insertAndDeleteUserEntityAndVerifyStorage() = runBlocking {
        val user = UserEntity(
            id = "sea",
            email = "deana.franklin@example.com",
            displayName = "Lincoln Franks",
            isLoggedIn = true,
            photoUrl = "https://www.database.com/img.jpg"
        )

        assertEquals(null, userDao.getLoggedInUser())
        userDao.insertUser(user)
        assert(userDao.getLoggedInUser() != null)
        assertEquals(user, userDao.getLoggedInUser())
        userDao.clearUsers()
        assertEquals(null, userDao.getLoggedInUser())
    }

    @After
    fun tearDown() {
        appDatabase.close()
    }
}