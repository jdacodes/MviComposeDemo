package com.jdacodes.mvicomposedemo.dashboard.presentation

sealed class DashboardUiEffect {
    data class ShowToast(val message: String) : DashboardUiEffect()
}