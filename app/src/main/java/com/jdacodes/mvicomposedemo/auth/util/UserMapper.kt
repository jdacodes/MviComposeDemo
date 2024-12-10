package com.jdacodes.mvicomposedemo.auth.util

import com.jdacodes.mvicomposedemo.auth.data.local.entity.UserEntity
import com.jdacodes.mvicomposedemo.auth.domain.model.User

fun User.toUserEntity(isLoggedIn: Boolean): UserEntity {
    return UserEntity(
        email = this.email,
        displayName = this.displayName,
        isLoggedIn = isLoggedIn,
        id = this.id
    )
}

fun UserEntity.toDomainUser(): User {
    return User(
        id = this.id ?: "",
        email = this.email ?: "",
        displayName = this.displayName ?: ""
    )
}
