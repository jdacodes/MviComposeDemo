package com.jdacodes.mvicomposedemo

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.jdacodes.mvicomposedemo.di.authModule
import com.jdacodes.mvicomposedemo.di.navigationModule
import com.jdacodes.mvicomposedemo.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.setApplicationId(BuildConfig.APP_ID_FACEBOOK)
        FacebookSdk.setClientToken(BuildConfig.CLIENT_TOKEN_FACEBOOK)
        FacebookSdk.sdkInitialize(applicationContext)
        AppEventsLogger.activateApp(this)
        FacebookSdk.fullyInitialize()
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(authModule, profileModule, navigationModule)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}