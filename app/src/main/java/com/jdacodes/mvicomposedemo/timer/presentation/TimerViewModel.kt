package com.jdacodes.mvicomposedemo.timer.presentation

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jdacodes.mvicomposedemo.auth.util.Constants.MILLISECONDS_IN_A_SECOND
import com.jdacodes.mvicomposedemo.auth.util.Constants.POMODORO_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.REST_TIMER_SECONDS
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerViewModel() : ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()
    private var pomodoroTimer: CountDownTimer? = null
    private val _uiEffect = Channel<TimerUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    init {
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
        }
    }

    private fun startTimer(seconds: Long) {
        Timber.d("startTimer: $seconds")
            try {
                Timber.d("Sending UI Effect: Timer started")
                viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Timer started")) }

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
                        viewModelScope.launch {  _uiEffect.send(TimerUiEffect.ShowToast("Timer finished"))}
                        pomodoroTimer?.cancel()
                        _timerState.update {
                            it.copy(
                                isPaused = true,
                                remainingSeconds =
                                if (it.lastTimer == TimerType.POMODORO)
                                    REST_TIMER_SECONDS
                                else
                                    POMODORO_TIMER_SECONDS,
                                lastTimer =
                                if (it.lastTimer == TimerType.POMODORO)
                                    TimerType.REST
                                else
                                    TimerType.POMODORO
                            )
                        }
                    }
                }
                Timber.d("Starting timer")
                pomodoroTimer?.start()
            } catch (e: Exception) {
                Timber.e(e, "Error while starting timer")
                viewModelScope.launch {  _uiEffect.send(
                    TimerUiEffect.ShowToast(
                        e.message ?: "Timer failed"
                    )
                )}

            }
    }

    private fun stopTimer() {
        _timerState.update {
            it.copy(
                isPaused = true
            )
        }
        pomodoroTimer?.cancel()
        viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Timer stopped")) }
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
        viewModelScope.launch { _uiEffect.send(TimerUiEffect.ShowToast("Timer reset")) }
    }

}



