package com.jdacodes.mvicomposedemo.core.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.navigation.HomeScreen
import com.jdacodes.mvicomposedemo.navigation.authNavGraph
import com.jdacodes.mvicomposedemo.navigation.util.HomeGraph

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController, modifier
        )
        composable<HomeGraph> {
            HomeScreen(
                rootNavController = navController,
                modifier = modifier
            )

        }

    }


}