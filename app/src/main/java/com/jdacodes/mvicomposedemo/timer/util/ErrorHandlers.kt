package com.jdacodes.mvicomposedemo.timer.util

import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

object ErrorHandlers {

    val showErrorExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    fun onError(error: Throwable) {
        Timber.e(error, "An error occurred") // Log the error using Timber
        // You can add other error handling logic here, like navigating to an error screen
    }
}