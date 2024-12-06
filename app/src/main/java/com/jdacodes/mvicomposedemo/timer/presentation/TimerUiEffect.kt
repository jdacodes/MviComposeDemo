package com.jdacodes.mvicomposedemo.timer.presentation

sealed class TimerUiEffect {
    data class ShowToast(val message: String) : TimerUiEffect()
    data object NavigateToHome : TimerUiEffect()
}