package com.jdacodes.mvicomposedemo.timer.presentation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.timer.domain.model.Session
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionList(
    viewModel: TimerViewModel,
    onSessionClick: (Session) -> Unit
) {
    val sessions by viewModel.sessions.collectAsState(initial = emptyMap()) // Use emptyMap as initial value
    val coroutineScope = rememberCoroutineScope() // Remember coroutine scope

    LaunchedEffect(sessions) { // Launch only once on composition
        coroutineScope.launch {
            viewModel.loadSession(viewModel.userId) // Trigger data loading
            Timber.d("LaunchedEffect is called")
        }
    }

    // Update sessionList only when sessions change
    val sessionList = remember(sessions) {
        mutableStateListOf<Session>().also { list ->
            list.addAll(sessions.values)
        }
    }

    Timber.d("SessionList: sessions map size: ${sessions.size}") // Log size of map in composable
    Timber.d("SessionList: sessionList size: ${sessionList.size}") // Log size of list in composable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.session_list)) },
            )
        }
    ) { paddingValues ->
        if (sessionList.isEmpty()) {
            // Display a message when the list is empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No sessions available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = sessionList, // Use sessionList directly for collection
                    key = { it.id }
                ) { session ->
                    Timber.tag("SessionList").d("Session size: ${sessionList.size}")
                    SessionListItem(session, onSessionClick)
                }
            }
        }
    }
}

@Composable
fun SessionListItem(
    session: Session,
    onSessionClick: (Session) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onSessionClick(session) },
        onClick = { onSessionClick(session) } // Redundant with clickable modifier
    ) {
        Column( // Use Column for vertical layout
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Pomodoros: ${session.pomodoro}")
            Spacer(modifier = Modifier.height(8.dp)) // Add spacing between items
            Text(
                text = if (session.completed) "Completed" else "Not Completed"
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Started: ${session.timeStarted}")
            if (session.completed) {
                Text(text = "Completed: ${session.timeCompleted}")
            }
        }
    }
}