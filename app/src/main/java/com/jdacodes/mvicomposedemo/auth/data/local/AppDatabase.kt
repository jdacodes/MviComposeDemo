package com.jdacodes.mvicomposedemo.auth.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jdacodes.mvicomposedemo.auth.data.local.entity.UserEntity
import com.jdacodes.mvicomposedemo.timer.data.local.StorageDao
import com.jdacodes.mvicomposedemo.timer.data.local.entity.SessionEntity

@Database(
    entities = [UserEntity::class, SessionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun storageDao(): StorageDao
}