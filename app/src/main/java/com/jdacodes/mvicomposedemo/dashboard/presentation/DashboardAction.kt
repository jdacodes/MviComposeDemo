package com.jdacodes.mvicomposedemo.dashboard.presentation

sealed class DashboardAction {
    data class LoadSessions(val userId: String) : DashboardAction()
}