package com.jdacodes.mvicomposedemo

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import org.koin.androidx.viewmodel.ext.android.viewModel

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.auth.presentation.splash.presentation.SplashViewModel
import com.jdacodes.mvicomposedemo.core.presentation.App
import com.jdacodes.mvicomposedemo.navigation.util.AuthGraph
import com.jdacodes.mvicomposedemo.navigation.util.HomeGraph
import com.jdacodes.mvicomposedemo.ui.theme.MviComposeDemoTheme

class MainActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen().apply {
            setOnExitAnimationListener { viewProvider ->
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleX",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 300
                    doOnEnd { viewProvider.remove() }
                    start()
                }
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleY",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = 300
                    doOnEnd { viewProvider.remove() }
                    start()
                }
            }
        }
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }
        enableEdgeToEdge()
        setContent {
            MviComposeDemoTheme {
                val navController = rememberNavController()

                //SplashViewModel states
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(
                    initialValue = true
                )
                val isLoggedIn by viewModel.eventState.collectAsStateWithLifecycle(
                    initialValue = false
                )

                if (isLoading) {
                    splashScreen.setKeepOnScreenCondition { true }
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                } else {
                    splashScreen.setKeepOnScreenCondition { false }
                    val startDestination = if (isLoggedIn) HomeGraph else AuthGraph
                    LaunchedEffect(Unit) {
                        navController.navigate(startDestination) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                    App(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .fillMaxSize(),
                        navController = navController,
                        startDestination = startDestination
                    )
                }

            }
        }
    }
}
