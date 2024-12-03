package com.jdacodes.mvicomposedemo.auth.presentation.timer.presentation

sealed class TimerAction {
    data class StartTimer(val seconds: Long) : TimerAction()
    data object StopTimer : TimerAction()
    data class ResetTimer(val seconds: Long) : TimerAction()
}