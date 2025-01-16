package com.jdacodes.mvicomposedemo.dashboard.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.navigation.util.Navigator
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import com.jdacodes.mvicomposedemo.timer.util.Constants.DATE_FORMAT
import com.jdacodes.mvicomposedemo.timer.util.Constants.ZONE_ID
import com.jdacodes.mvicomposedemo.timer.util.ErrorHandlers.onError
import com.jdacodes.mvicomposedemo.timer.util.ErrorHandlers.showErrorExceptionHandler
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class DashboardViewModel(
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _dashboardState = MutableStateFlow(DashboardState())
    val dashboardState = _dashboardState.asStateFlow()

    private val _uiEffect = Channel<DashboardUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
        getUserId()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadSessions -> loadSession(action.userId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getUserId() {
        viewModelScope.launch(showErrorExceptionHandler) {
            authRepository.getCurrentUser()
                .catch { e ->
                    _uiEffect.send(DashboardUiEffect.ShowToast("Error loading user details"))
                    Timber.e(e.message ?: "Error loading user details")
                }
                .collect { user ->
                    if (user != null) {
                        _uiEffect.send(DashboardUiEffect.ShowToast("User is not null"))
                        Timber.d("User id: ${user.id}")
                        _dashboardState.update { currentState ->
                            currentState.copy(
                                userId = user.id
                            )
                        }
                        val userId = _dashboardState.value.userId
                        loadSession(userId)
                    } else {
                        Timber.d("User is null")
                    }
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSession(userId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            try {
                val sessionsList = storageRepository.getSessionsByUserId(userId)
                Timber.d("Loaded ${sessionsList.size} sessions for user $userId")
                Timber.d("Sessions retrieved: $sessionsList")

                _dashboardState.update { currentState ->
                    currentState.copy(
                        sessions = sessionsList.associateBy { it.id }
                    )
                }
                updateDailyFrequencies()
                Timber.d("Session in map:")
                sessionsList.forEach { session ->
                    Timber.d("  ID=${session.id}, Session=$session")
                }

            } catch (e: Exception) {
                Timber.e("Error loading sessions: ${e.message}")
                _uiEffect.send(DashboardUiEffect.ShowToast("Failed to load sessions"))
            }
        }
    }

    fun addListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            val userId = _dashboardState.value.userId
            storageRepository.addListener(
                userId, ::onDocumentEvent, ::onError
            )
        }
    }

    fun removeListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageRepository.removeListener()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onDocumentEvent(wasDocumentDeleted: Boolean, session: Session) {
        val currentSessions = _dashboardState.value.sessions
        _dashboardState.update { currentState ->
            if (wasDocumentDeleted) {
                currentState.copy(
                    sessions = currentSessions - session.id
                )
            } else {
                currentState.copy(
                    sessions = currentSessions + (session.id to session)
                )
            }
        }
        updateDailyFrequencies()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDailyFrequencies(sessions: List<Session>): List<DailyFrequency> {
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of(ZONE_ID))
        val dayFrequencyMap = mutableMapOf<String, Int>()
        Timber.d("Daily Frequencies: $dayFrequencyMap")

        sessions.forEach { session ->
            try {
                // Parse timeStarted as ZonedDateTime
                val zonedDateTime = ZonedDateTime.parse(session.timeStarted, formatter)
                // Get day of the week in short format (e.g., "Mon", "Tue")
                val dayOfWeek = zonedDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                dayFrequencyMap[dayOfWeek] = dayFrequencyMap.getOrDefault(dayOfWeek, 0) + session.pomodoro
            } catch (e: Exception) {
                Timber.e("Invalid date format or time zone for session: ${session.id}, error: ${e.message}")
            }
        }

        // Ensure all days of the week are present, even if frequency is 0
        return daysOfWeek.map { day ->
            DailyFrequency(day, dayFrequencyMap[day] ?: 0)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDailyFrequencies() {
        Timber.d("Updating daily frequencies")
        val sessions = _dashboardState.value.sessions.values.toList()
        val frequencies = calculateDailyFrequencies(sessions)
        _dashboardState.update { it.copy(dailyFrequencies = frequencies) }
    }

}
