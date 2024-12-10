package com.jdacodes.mvicomposedemo.auth.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jdacodes.mvicomposedemo.auth.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM ` user` WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("DELETE FROM ` user`")
    suspend fun clearUsers()
}