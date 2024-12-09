package com.jdacodes.mvicomposedemo.timer.presentation

import android.content.Context

sealed class TimerAction {
    data class StartTimer(val seconds: Long) : TimerAction()
    data object StopTimer : TimerAction()
    data class ResetTimer(val seconds: Long) : TimerAction()
    data class ShowNotification(val context: Context,val title: String, val content: String) : TimerAction()
}