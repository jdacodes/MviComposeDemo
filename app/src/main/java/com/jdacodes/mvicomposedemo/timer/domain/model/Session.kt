package com.jdacodes.mvicomposedemo.timer.domain.model

data class Session(
    val id: String = "",
    val userId: String = "",
    val pomodoro: Int = 0,
    val completed: Boolean = false,
    val timeStarted: String = ""
)
