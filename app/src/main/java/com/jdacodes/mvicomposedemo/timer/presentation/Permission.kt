package com.jdacodes.mvicomposedemo.timer.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

@Composable
fun NotificationPermission(context: Context) {
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }
    //Activity Result Launcher instead of Accompanist Library
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            //If both returned false, user has declined the permission permanently
            //Redirect user to settings to enable notification to show notifications
//                            if(!isGranted) {
//                                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
//                            }
            //Explain why there is a need for permission
        })

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
