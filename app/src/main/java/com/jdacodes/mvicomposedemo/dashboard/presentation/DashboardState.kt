package com.jdacodes.mvicomposedemo.dashboard.presentation

import com.jdacodes.mvicomposedemo.timer.domain.model.Session

data class DashboardState(
    val dailyFrequencies: List<DailyFrequency> = emptyList(),
    val sessions: Map<String, Session> = emptyMap(),
    val userId: String = ""
)

data class DailyFrequency(
    val day: String,
    val frequency: Int
)