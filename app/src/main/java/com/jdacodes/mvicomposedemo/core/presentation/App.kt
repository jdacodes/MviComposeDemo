package com.jdacodes.mvicomposedemo.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jdacodes.mvicomposedemo.navigation.HomeScreen
import com.jdacodes.mvicomposedemo.navigation.authNavGraph
import com.jdacodes.mvicomposedemo.navigation.util.AuthGraph
import com.jdacodes.mvicomposedemo.navigation.util.HomeGraph

@Composable
fun App(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AuthGraph
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