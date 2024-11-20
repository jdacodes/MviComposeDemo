package com.jdacodes.mvicomposedemo.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    state: ProfileState,
    modifier: Modifier = Modifier,
    uiEffect: Flow<ProfileUiEffect>,
    onAction: (ProfileAction) -> Unit
) {
    val context = LocalContext.current
    // Handle UI effects (e.g., showing toast)
    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is ProfileUiEffect.ShowToast -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        onAction(ProfileAction.DisplayUserDetails)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Profile") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        },
        modifier = modifier.padding(16.dp)
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
        ) {
            when (state) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator() // Show loading indicator
                }

                is ProfileState.Success -> {
                    ProfileContent(
                        user = state.user,
                        onAction = onAction,
                        modifier = modifier
                    )

                }
            }
        }

    }
}

@Composable
private fun ProfileContent(
    user: User?,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("ID: ${user?.id}", style = MaterialTheme.typography.bodyMedium)
            Text("Email: ${user?.email}", style = MaterialTheme.typography.bodyMedium)
            Text("Display name: ${user?.username}", style = MaterialTheme.typography.bodyMedium)
            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { onAction(ProfileAction.DisplayUserDetails) },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("Reload User Details")
                }
            }
        }


    }
}