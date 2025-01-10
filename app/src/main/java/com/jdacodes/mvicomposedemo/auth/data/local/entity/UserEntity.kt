package com.jdacodes.mvicomposedemo.auth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = " user")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val displayName: String,
    val isLoggedIn: Boolean = false,
    val photoUrl: String? = null,
)
