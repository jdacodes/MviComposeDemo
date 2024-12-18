package com.jdacodes.mvicomposedemo.timer.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.util.Constants.LONG_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.POMODORO_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.auth.util.Constants.SECONDS_IN_A_MINUTE
import com.jdacodes.mvicomposedemo.auth.util.Constants.SHORT_BREAK_TIMER_SECONDS
import com.jdacodes.mvicomposedemo.timer.util.pad
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    timerState: TimerState,
    onAction: (TimerAction) -> Unit,
    uiEffect: Flow<TimerUiEffect>,
    viewModel: TimerViewModel,
) {
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED &&
                        NotificationManagerCompat.from(context).areNotificationsEnabled()
            } else {
                NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        )
    }

// State to show settings dialog
    var showSettingsDialog by remember { mutableStateOf(false) }
    //Activity Result Launcher instead of Accompanist Library
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            //If both returned false, user has declined the permission permanently
            //Redirect user to settings to enable notification to show notifications
            if (!isGranted && !shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            ) {
                showSettingsDialog = true
            }
            //Explain why there is a need for permission
        })
    LaunchedEffect(true) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val timerSeconds =
        if (timerState.lastTimer == TimerType.POMODORO)
            POMODORO_TIMER_SECONDS
        else {
            if (timerState.pomodoroCount % 4 == 0 && timerState.pomodoroCount != 0) {
                LONG_BREAK_TIMER_SECONDS
            } else
                SHORT_BREAK_TIMER_SECONDS
        }
    val mediaPlayer = MediaPlayer.create(LocalContext.current, R.raw.bell_sound)
    LaunchedEffect(key1 = timerState.remainingSeconds) {
        if (timerState.remainingSeconds == 0L)
            mediaPlayer.start()
    }

    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is TimerUiEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    if (hasNotificationPermission) {
                        onAction(
                            TimerAction.ShowNotification(
                                context, context.getString(R.string.pomodoro_timer), effect.message
                            )
                        )
                    }
                }
                // Remove navigation handling from here as it will be handled through state
                else -> {}
            }
        }
    }
    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.pomodoro_timer)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        if (showSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showSettingsDialog = false },
                title = { Text(stringResource(R.string.notification_permission_required)) },
                text = { Text(stringResource(R.string.alertdialog_notification_content)) },
                confirmButton = {
                    TextButton(onClick = {
                        // Open app specific settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        showSettingsDialog = false
                    }) {
                        Text(stringResource(R.string.open_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSettingsDialog = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
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
//                        text = "Timer\n$minutes : $seconds",
                        text = "Timer\n${minutes.pad()} : ${seconds.pad()}",
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
                    IconButton(onClick = {
                        onAction(TimerAction.SessionCompleted)
                        onAction(TimerAction.StopTimer)
                        onAction(TimerAction.ResetTimer(POMODORO_TIMER_SECONDS))
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "Mark Session Completed",
                        )
                    }

                }
            }
        }
    }
}