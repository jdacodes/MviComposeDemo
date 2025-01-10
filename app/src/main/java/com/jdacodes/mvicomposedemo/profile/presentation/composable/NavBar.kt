package com.jdacodes.mvicomposedemo.profile.presentation.composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.profile.presentation.ProfileAction
import com.jdacodes.mvicomposedemo.profile.presentation.connection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavBar(
    user: User?,
    onAction: (ProfileAction) -> Unit,
    onSignOutClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    titleText: String = "Profile"
) {
    var alphaValue by remember { mutableFloatStateOf(0f) }

    alphaValue = (3 * (1f - connection.progress)).coerceIn(0f, 1f)
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Text(
                text = "Hey there, ${user?.displayName ?: "User"}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            // Three dots menu
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = alphaValue)
                )
            }

            // Dropdown menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sign out") },
                    onClick = {
                        showMenu = false
                        onSignOutClick()
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = alphaValue),
            titleContentColor = MaterialTheme.colorScheme.primary.copy(alpha = alphaValue),
            actionIconContentColor = MaterialTheme.colorScheme.primary.copy(alpha = alphaValue)
        ),
        modifier = modifier
    )

}