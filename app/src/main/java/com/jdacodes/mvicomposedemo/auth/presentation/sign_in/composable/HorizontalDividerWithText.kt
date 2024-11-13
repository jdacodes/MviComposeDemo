package com.jdacodes.mvicomposedemo.auth.presentation.sign_in.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalDividerWithText(
    modifier: Modifier = Modifier,
    text: String = "or",
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = dividerColor
        )
        Text(
            text = text,
            color = textColor,
            style = textStyle,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = dividerColor
        )
    }
}

// Example usage:
@Preview(showBackground = true)
@Composable
fun PreviewDivider() {
    MaterialTheme {
        Surface {
            HorizontalDividerWithText(
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}