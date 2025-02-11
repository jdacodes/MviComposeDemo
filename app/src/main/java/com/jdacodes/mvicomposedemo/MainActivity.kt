package com.jdacodes.mvicomposedemo

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.auth.presentation.splash.presentation.SplashViewModel
import com.jdacodes.mvicomposedemo.core.presentation.App
import com.jdacodes.mvicomposedemo.navigation.util.AuthGraph
import com.jdacodes.mvicomposedemo.navigation.util.HomeGraph
import com.jdacodes.mvicomposedemo.ui.theme.MviComposeDemoTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SplashViewModel by viewModel()
    @SuppressLint("NewApi")
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
        val windowCompat = WindowCompat.getInsetsController(window, window.decorView)
        windowCompat.hide(WindowInsetsCompat.Type.navigationBars())
        windowCompat.hide(WindowInsetsCompat.Type.statusBars())
        windowCompat.systemBarsBehavior =WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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
                            .fillMaxSize().statusBarsPadding(),
                        navController = navController,
                        startDestination = startDestination
                    )
                }

            }
        }
    }
}
