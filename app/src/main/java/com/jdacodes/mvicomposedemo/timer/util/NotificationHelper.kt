package com.jdacodes.mvicomposedemo.timer.util

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.core.util.CHANNEL_ID


fun showNotification(
    context: Context,
    title: String,
    content: String
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentText(content)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    notificationManager.notify(1, notification)
}



