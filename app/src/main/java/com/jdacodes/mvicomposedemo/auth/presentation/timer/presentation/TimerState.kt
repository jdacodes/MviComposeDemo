package com.jdacodes.mvicomposedemo.auth.presentation.timer.presentation

data class TimerState(
    val remainingSeconds: Long = 0L,
    val isPaused: Boolean = true,
    val lastTimer: TimerType = TimerType.POMODORO,
)
enum class TimerType {
    POMODORO,
    REST,
}