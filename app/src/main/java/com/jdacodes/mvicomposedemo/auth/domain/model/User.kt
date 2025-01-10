package com.jdacodes.mvicomposedemo.auth.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null

)
