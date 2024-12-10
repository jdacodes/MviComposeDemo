package com.jdacodes.mvicomposedemo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.jdacodes.mvicomposedemo.core.util.CHANNEL_ID
import com.jdacodes.mvicomposedemo.di.authModule
import com.jdacodes.mvicomposedemo.di.databaseModule
import com.jdacodes.mvicomposedemo.di.navigationModule
import com.jdacodes.mvicomposedemo.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }

        FacebookSdk.setApplicationId(BuildConfig.APP_ID_FACEBOOK)
        FacebookSdk.setClientToken(BuildConfig.CLIENT_TOKEN_FACEBOOK)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        FacebookSdk.fullyInitialize()
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(authModule, profileModule, navigationModule, databaseModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}