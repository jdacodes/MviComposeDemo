package com.jdacodes.mvicomposedemo.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController


@Composable
fun HomeScreen(
    logout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    var currentDestination by rememberSaveable { mutableStateOf(HomeDestinations.PROFILE) }

    val windowSize = with(LocalDensity.current) {
        currentWindowSize().toSize().toDpSize()
    }
    val layoutType = if (windowSize.width >= 1200.dp) {
        NavigationSuiteType.NavigationDrawer
    } else {
        NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
            currentWindowAdaptiveInfo()
        )
    }

    NavigationSuiteScaffold(
        layoutType = layoutType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.primaryContainer,
            navigationBarContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationDrawerContainerColor = MaterialTheme.colorScheme.surface,
            navigationDrawerContentColor = MaterialTheme.colorScheme.onSurface,
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationRailContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        navigationSuiteItems = {
            HomeDestinations.entries.forEach { destination ->
                item(
                    icon = {
                        if(destination == currentDestination){
                            Icon(
                                destination.iconSelected,
                                contentDescription = stringResource(destination.contentDescription)
                            )
                        } else {
                            Icon(
                                destination.iconUnselected,
                                contentDescription = stringResource(destination.contentDescription)
                            )
                        }

                    },
                    label = { Text(stringResource(destination.label)) },
                    selected = destination == currentDestination,
                    onClick = {
                        navController.navigate(destination.route) {
                            currentDestination = destination
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    ) {
        HomeNavGraph(
            navController = navController,
            logout = logout,
            modifier = modifier
        )
    }
}