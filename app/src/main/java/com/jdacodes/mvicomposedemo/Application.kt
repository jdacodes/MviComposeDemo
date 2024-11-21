package com.jdacodes.mvicomposedemo

import android.app.Application
import com.jdacodes.mvicomposedemo.di.authModule
import com.jdacodes.mvicomposedemo.di.navigationModule
import com.jdacodes.mvicomposedemo.di.profileModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class Application : Application() {
    override fun onCreate() {
        startKoin {
            androidContext(this@Application)
            androidLogger()
            modules(authModule, profileModule, navigationModule)
        }
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}