package com.jdacodes.mvicomposedemo.dashboard.presentation

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.dashboard.presentation.composable.WeeklyBarChart
import kotlinx.coroutines.flow.Flow

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardState: DashboardState,
    modifier: Modifier = Modifier,
    onAction: (DashboardAction) -> Unit,
    uiEffect: Flow<DashboardUiEffect>,
    viewModel: DashboardViewModel,
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is DashboardUiEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }
                // Remove navigation handling from here as it will be handled through state
                else -> {}
            }
        }
    }

    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    actionIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeeklyBarChart(
                    data = dashboardState.dailyFrequencies,
                    barColor = MaterialTheme.colorScheme.primary,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
        }

    }
}