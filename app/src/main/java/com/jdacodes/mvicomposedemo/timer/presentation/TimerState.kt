package com.jdacodes.mvicomposedemo.timer.presentation

data class TimerState(
    val remainingSeconds: Long = 0L,
    val isPaused: Boolean = true,
    val lastTimer: TimerType = TimerType.POMODORO,
    val pomodoroCount: Int = 0,
)

enum class TimerType {
    POMODORO,
    SHORT_BREAK,
    LONG_BREAK
}