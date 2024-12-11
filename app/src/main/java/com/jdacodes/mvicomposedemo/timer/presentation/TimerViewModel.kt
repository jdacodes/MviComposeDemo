package com.jdacodes.mvicomposedemo.timer.presentation

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.util.Constants.LONG_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.MILLISECONDS_IN_A_SECOND
import com.jdacodes.mvicomposedemo.auth.util.Constants.POMODORO_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.SHORT_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
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

class TimerViewModel(
    private val storageService: StorageService,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()
    private var pomodoroTimer: CountDownTimer? = null
    private var pomodoroCount by mutableIntStateOf(0)
    private val _uiEffect = Channel<TimerUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()
    val sessions = mutableStateMapOf<String, Session>()
    private var userId by mutableStateOf("")

    init {
        getUserId()
        _timerState.update {
            it.copy(
                remainingSeconds = POMODORO_TIMER_SECONDS,
                lastTimer = TimerType.POMODORO
            )
        }
    }

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
        }
    }

    private fun startTimer(seconds: Long) {
        Timber.d("startTimer: $seconds")
        try {
            Timber.d("Sending UI Effect: Timer started")
            if (_timerState.value.lastTimer == TimerType.POMODORO) {
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro started")) }
            } else {
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Break started")) }
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
                    _timerState.update {
                        it.copy(
                            isPaused = true,
                            remainingSeconds =
                            if (it.lastTimer == TimerType.POMODORO) {
                                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Pomodoro finished")) }
                                if (pomodoroCount != 0 && pomodoroCount % 4 == 0) {
                                    LONG_BREAK_TIMER_SECONDS
                                } else {
                                    SHORT_BREAK_TIMER_SECONDS
                                }
                            } else {
                                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Break finished")) }
                                POMODORO_TIMER_SECONDS
                            },
                            lastTimer =
                            if (it.lastTimer == TimerType.POMODORO)
                                TimerType.REST
                            else
                                TimerType.POMODORO,
                            pomodoroCount = pomodoroCount++
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
        } else {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Break stopped")) }
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
        } else {
            viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Break reset")) }
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
                    } else {
                        Timber.d("User is null")
                    }
                }
        }
    }

    fun addListener() {
        viewModelScope.launch(showErrorExceptionHandler) {

            storageService.addListener(userId, ::onDocumentEvent, ::onError)
        }
    }

    fun removeListener() {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.removeListener()
        }
    }

    fun updateSession(session: Session) {
        viewModelScope.launch(showErrorExceptionHandler) {
            val updatedSession = session.copy(completed = !session.completed)
            storageService.updateSession(updatedSession) { error ->
                if (error != null) {
                    Timber.e(error.message ?: "Error updating session")
                    onError(error)
                }
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Session updated")) }
                Timber.d("Session updated successfully")
            }
        }
    }

    fun saveSession(session: Session) {
        viewModelScope.launch(showErrorExceptionHandler) {
            storageService.saveSession(session) { error ->
                if (error != null) {
                    Timber.e(error.message ?: "Error saving session")
                    onError(error)
                }
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Session saved")) }
            }
        }
    }

    private fun onDocumentEvent(wasDocumentDeleted: Boolean, session: Session) {
        if (wasDocumentDeleted) sessions.remove(session.id) else sessions[session.id] = session
    }
}



