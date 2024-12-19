package com.jdacodes.mvicomposedemo.timer.presentation

import android.annotation.SuppressLint
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.timer.util.Constants.LONG_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.timer.util.Constants.MILLISECONDS_IN_A_SECOND
import com.jdacodes.mvicomposedemo.timer.util.Constants.POMODORO_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.timer.util.Constants.SHORT_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import com.jdacodes.mvicomposedemo.timer.util.ErrorHandlers.onError
import com.jdacodes.mvicomposedemo.timer.util.ErrorHandlers.showErrorExceptionHandler
import com.jdacodes.mvicomposedemo.timer.util.showNotification
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
import java.util.Locale

class TimerViewModel(
    private val storageRepository: StorageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()
    private var pomodoroTimer: CountDownTimer? = null
    private var pomodoroCount by mutableIntStateOf(0)
    private val _uiEffect = Channel<TimerUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    var sessions = mutableStateMapOf<String, Session>()
    private var userId by mutableStateOf("")
    var currentSession: Session? = null
    private var previousSession: Session? = null

    init {
        getUserId()
        _timerState.update {
            it.copy(
                remainingSeconds = POMODORO_TIMER_SECONDS,
                lastTimer = TimerType.POMODORO
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAction(action: TimerAction) {
        when (action) {
            is TimerAction.StartTimer -> {
                startTimer(action.seconds)
                Timber.d("StartTimer: startTimer(${action.seconds}) ")
            }

            TimerAction.StopTimer -> stopTimer()
            is TimerAction.ResetTimer -> resetTimer(action.seconds)
            is TimerAction.ShowNotification -> showNotification(
                action.context,
                action.title,
                action.content
            )
            is TimerAction.SessionCompleted -> toggleSessionCompletion()
        }
    }

    private fun startTimer(seconds: Long) {
        Timber.d("startTimer: $seconds")
        try {
            Timber.d("Sending UI Effect: Timer started")
            if (_timerState.value.lastTimer == TimerType.POMODORO) {
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro started")) }
            } else if (_timerState.value.lastTimer == TimerType.SHORT_BREAK) {
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Short break started")) }
            } else {
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Long break started")) }
            }

            _timerState.update {
                it.copy(
                    isPaused = false
                )
            }
            Timber.d("Initializing CountDownTimer for $seconds seconds")
            pomodoroTimer = object : CountDownTimer(
                seconds * MILLISECONDS_IN_A_SECOND,
                MILLISECONDS_IN_A_SECOND
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    Timber.d("onTick() - millisUntilFinished: $millisUntilFinished")
                    _timerState.update {
                        it.copy(
                            remainingSeconds = millisUntilFinished / MILLISECONDS_IN_A_SECOND
                        )
                    }
                }

                override fun onFinish() {
                    Timber.d("onFinish() - Timer finished")
                    pomodoroTimer?.cancel()

                    val currentTimerType = _timerState.value.lastTimer // Get current timer type

                    if (currentTimerType == TimerType.POMODORO) { // Check if it was a Pomodoro that finished
                        pomodoroCount++ // Increment BEFORE saving the session
                        onPomodoroFinished() // Save the session immediately
                        viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro finished")) }

                    } else if (currentTimerType == TimerType.SHORT_BREAK) {
                        viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Short Break finished")) }
                    } else {
                        viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Long Break finished")) }
                    }

                    val nextTimerType = when (currentTimerType) { // Determine the next timer type
                        TimerType.POMODORO -> {
                            if (pomodoroCount % 4 == 0) {
                                TimerType.LONG_BREAK
                            } else {
                                TimerType.SHORT_BREAK
                            }
                        }

                        TimerType.SHORT_BREAK, TimerType.LONG_BREAK -> TimerType.POMODORO
                    }

                    _timerState.update {
                        it.copy(
                            isPaused = true,
                            remainingSeconds = when (nextTimerType) {
                                TimerType.POMODORO -> POMODORO_TIMER_SECONDS
                                TimerType.SHORT_BREAK -> SHORT_BREAK_TIMER_SECONDS
                                TimerType.LONG_BREAK -> LONG_BREAK_TIMER_SECONDS
                            },
                            lastTimer = nextTimerType,
                            pomodoroCount = pomodoroCount // Update the state with the correct count
                        )
                    }
                    Timber.d("Pomodoro count Int State: $pomodoroCount")
                    Timber.d("Pomodoro count UI State: ${timerState.value.pomodoroCount}")
                }
            }
            Timber.d("Starting timer")
            pomodoroTimer?.start()
        } catch (e: Exception) {
            Timber.e(e, "Error while starting timer")
            viewModelScope.launch {
                _uiEffect.send(
                    TimerUiEffect.ShowToast(
                        e.message ?: "Timer failed"
                    )
                )
            }

        }
    }

    private fun stopTimer() {
        _timerState.update {
            it.copy(
                isPaused = true
            )
        }
        pomodoroTimer?.cancel()
        if (_timerState.value.lastTimer == TimerType.POMODORO) {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro stopped")) }
        } else if (_timerState.value.lastTimer == TimerType.SHORT_BREAK) {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Short break stopped")) }
        } else {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Long break stopped")) }
        }
    }

    private fun resetTimer(seconds: Long) {
        pomodoroTimer?.cancel()
        _timerState.update {
            it.copy(
                isPaused = true,
                remainingSeconds = seconds,
                lastTimer = TimerType.POMODORO
            )
        }
        if (_timerState.value.lastTimer == TimerType.POMODORO) {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro reset")) }
        } else if (_timerState.value.lastTimer == TimerType.SHORT_BREAK) {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Short break reset")) }
        } else {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Long break reset")) }
        }
    }

    private fun getUserId() {
        viewModelScope.launch(showErrorExceptionHandler) {
            authRepository.getCurrentUser()
                .catch { e ->
                    _uiEffect.send(TimerUiEffect.ShowToast("Error loading user details"))
                    Timber.e(e.message ?: "Error loading user details")
                }
                .collect { user ->
                    if (user != null) {
                        _uiEffect.send(TimerUiEffect.ShowToast("User is not null"))
                        Timber.d("User id: ${user.id}")
                        userId = user.id
                        loadSession(user.id)
                    } else {
                        Timber.d("User is null")
                    }
                }
        }
    }

    fun addListener() {
        viewModelScope.launch(showErrorExceptionHandler) {

            storageRepository.addListener(userId, ::onDocumentEvent, ::onError)
        }
    }

    fun removeListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageRepository.removeListener()
        }
    }

    private fun loadSession(userId: String) {
        viewModelScope.launch(showErrorExceptionHandler) {
            try {
                val sessions = storageRepository.getSessionsByUserId(userId)
                Timber.d("Loaded ${sessions.size} sessions for user $userId")

                // Update UI state or perform further actions
//                sessions = sessions
                currentSession = if (sessions.isNotEmpty()) {
                 sessions.first().copy(userId = userId)
                } else {
                    Session(userId = userId)
                }
                saveSession()
            }catch (e: Exception) {
                Timber.e("Error loading sessions: ${e.message}")
                _uiEffect.send(TimerUiEffect.ShowToast("Failed to load sessions"))
            }
        }
    }

    private fun saveSession() {
        currentSession?.let { session ->
            viewModelScope.launch(showErrorExceptionHandler) {
                try {
                    val newSessionId = storageRepository.saveSession(session)
                    Timber.d("Session saved with ID: $newSessionId")
                    currentSession = session.copy(id = newSessionId)
                    // Update the sessions map
                    if (newSessionId.isNotEmpty()) {
                        sessions[newSessionId] = currentSession!!
                    }
                    _uiEffect.send(TimerUiEffect.ShowToast("Session saved"))
                } catch (e: Exception) {
                    // Error handling
                    Timber.e(e.message ?: "Error saving session")
                    onError(e)
                    _uiEffect.send(TimerUiEffect.ShowToast("Error saving session"))
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun onPomodoroFinished() {
        val currentTime = ZonedDateTime.now(ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val formattedTime = currentTime.format(formatter)

        currentSession = if (currentSession == null || currentSession?.timeStarted.isNullOrBlank()) {
            Session(
                id = currentSession?.id ?: "", // Preserve existing ID if any
                userId = userId,
                pomodoro = pomodoroCount,
                timeStarted = formattedTime,
                completed = false
            )
        } else {
            // Update existing session
            currentSession?.copy(
                pomodoro = pomodoroCount,
                timeStarted = formattedTime
            )
        }
        saveSession()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun toggleSessionCompletion() {
        viewModelScope.launch(showErrorExceptionHandler) {
            try {
                val session = currentSession ?: return@launch

                // Mark the current session as completed
                val completedSession = session.copy(
                    completed = true,
                    // Optionally add a timestamp for when it was completed
                    timeCompleted = ZonedDateTime.now(ZoneId.of("UTC"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                )
                // Save the completed session
                storageRepository.saveSession(completedSession)
                                // Create a new session
                val currentTime = ZonedDateTime.now(ZoneId.of("UTC"))
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val formattedTime = currentTime.format(formatter)

                val newSession = Session(
                    userId = userId,
                    pomodoro = 0,  // Reset pomodoro count
                    timeStarted = formattedTime,
                    completed = false,
                    timeCompleted = ""
                )
                // Save the new session and update current session
                val newSessionId = storageRepository.saveSession(newSession)
                currentSession = newSession.copy(id = newSessionId)
                sessions[newSessionId] = currentSession!! // Update local map
                _timerState.update { it.copy(pomodoroCount = 0) } // Reset UI state
                pomodoroCount = 0 // Reset ViewModel count
                // Send UI feedback
                _uiEffect.send(
                    TimerUiEffect.ShowToast("Previous session completed. New session started.")
                )
            } catch (e: Exception) {
                Timber.e("Error toggling session completion: ${e.message}")
                _uiEffect.send(TimerUiEffect.ShowToast("Failed to update session"))
            }
        }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, session: Session) {
        if (wasDocumentDeleted) sessions.remove(session.id) else sessions[session.id] = session
        sessions.entries.forEach { Timber.d("Session: ${it.value}") }
    }
}



