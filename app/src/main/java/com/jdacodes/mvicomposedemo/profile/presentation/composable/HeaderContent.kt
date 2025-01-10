package com.jdacodes.mvicomposedemo.profile.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.auth.domain.model.User

@Composable
fun HeaderContent(
    user: User
) {
    HeaderItem(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(8.dp),
        user.displayName,
        style = MaterialTheme.typography.titleLarge
    )



    HeaderItem(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(8.dp),
        user.email,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun HeaderItem(modifier: Modifier = Modifier, title: String, style: TextStyle) {
    Box(modifier = modifier, contentAlignment = Alignment.CenterStart) {
        Text(
            text = title,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = style
        )
    }
}
