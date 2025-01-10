package com.jdacodes.mvicomposedemo.profile.presentation.composable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileAction
import com.jdacodes.mvicomposedemo.profile.presentation.connection

@Composable
fun CollapsibleContainer(
    user: User?,
    onAction: (ProfileAction) -> Unit,
    contents: List<String>,
    innerPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection)
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.scrollable(
                orientation = Orientation.Vertical,
                // state for Scrollable, describes how consume scroll amount
                state =
                rememberScrollableState { delta ->
                    0f
                }
            )) {
                ExpandedHeader(
                    user = user,
                    onAction = onAction,
                    modifier = Modifier,
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(weight = 1f)
                ) {
                    items(contents) {
                        ListItem(headlineContent = {
                            Text(text = it)
                        })
                    }
                }
            }
        }
    }
}