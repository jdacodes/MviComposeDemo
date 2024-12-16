package com.jdacodes.mvicomposedemo.di

import androidx.room.Room
import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.auth.data.AuthenticationManager
import com.jdacodes.mvicomposedemo.auth.data.local.AppDatabase
import com.jdacodes.mvicomposedemo.auth.data.repository.AuthRepositoryImpl
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.timer.data.local.repository.StorageRepositoryImpl
import com.jdacodes.mvicomposedemo.timer.data.remote.StorageServiceImpl
import com.jdacodes.mvicomposedemo.timer.domain.StorageRepository
import com.jdacodes.mvicomposedemo.timer.domain.StorageService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    // Provide the Room database instance
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration() // Optional: handle migrations
            .build()
    }

    // Provide the UserDao as a single instance
    single {
        get<AppDatabase>().userDao()
    }
    single {
        get<AppDatabase>().storageDao()
    }
}

val authModule = module {

    single {
        AuthenticationManager(
            context = androidContext(),
            webClientId = BuildConfig.WEB_CLIENT_ID_FIREBASE
        )
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            authenticationManager = get(),
            userDao = get()
        )
    }

    single<StorageService>{
        StorageServiceImpl()
    }

    single<StorageRepository> {
        StorageRepositoryImpl(
            storageService = get(),
            storageDao = get()
        )
    }

}
