package com.jdacodes.mvicomposedemo.auth.presentation.timer.presentation

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginUiEffect
import com.jdacodes.mvicomposedemo.auth.util.Constants.POMODORO_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.REST_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.SECONDS_IN_A_MINUTE
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    timerViewModel: TimerViewModel,
    timerState: TimerState,
    onAction: (TimerAction) -> Unit,
    uiEffect: Flow<TimerUiEffect>,
) {
    val timerSeconds =
        if (timerState.lastTimer == TimerType.POMODORO)
            POMODORO_TIMER_SECONDS
        else
            REST_TIMER_SECONDS
    val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.bell_sound)
    LaunchedEffect(key1 = timerState.remainingSeconds) {
        if (timerState.remainingSeconds == 0L)
            mediaPlayer.start()
    }
    val context = LocalContext.current
    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is TimerUiEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                // Remove navigation handling from here as it will be handled through state
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pomodoro Timer") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val progress =
                timerState.remainingSeconds.toFloat() / (timerSeconds.toFloat())
            val minutes = timerState.remainingSeconds / SECONDS_IN_A_MINUTE
            val seconds = timerState.remainingSeconds - (minutes * SECONDS_IN_A_MINUTE)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = {
                            progress
                        },
                        modifier = Modifier
                            .width(256.dp)
                            .height(256.dp),
                        trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    )
                    Text(
                        text = "Timer\n$minutes : $seconds",
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        onClick = {
                            if (timerState.isPaused) {
                                onAction(TimerAction.StartTimer(timerState.remainingSeconds))
                            } else {
                                onAction(TimerAction.StopTimer)
                            }
                        },
                    ) {
                        Icon(
                            imageVector = if (timerState.isPaused) Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                            contentDescription = null

                        )
                    }
                    IconButton(
                        modifier = Modifier.padding(bottom = 8.dp),
                        onClick = {
                            onAction(TimerAction.ResetTimer(POMODORO_TIMER_SECONDS))
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                    }
                }
            }
        }
    }
}