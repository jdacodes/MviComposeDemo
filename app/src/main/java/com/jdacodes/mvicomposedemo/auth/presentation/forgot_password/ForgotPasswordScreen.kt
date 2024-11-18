package com.jdacodes.mvicomposedemo.auth.presentation.forgot_password

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.ForgotPasswordState
import com.jdacodes.mvicomposedemo.ui.theme.MviComposeDemoTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow


@Composable
fun ForgotPasswordScreen(
    state: AuthState,
    uiEffect: Flow<ForgotPasswordUiEffect>,
    onAction: (ForgotPasswordAction) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTopBar by remember { mutableStateOf(true) }

    // Handle UI effects
    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is ForgotPasswordUiEffect.ShowToast -> {
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

    //Handle one-time events
    LaunchedEffect(state) {
        when (state) {
            is AuthState.Sent -> {
                onAction(ForgotPasswordAction.NavigateToLogin)
            }

            else -> { /* do nothing */
            }
        }
    }
    Scaffold(
        topBar = {
            if (showTopBar) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = "Forgot Password",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Enter your email to reset your password",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                }
            }

        },
        modifier = modifier.padding(16.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (state) {
                is ForgotPasswordState -> {
                    showTopBar = true
                    val formState = state as ForgotPasswordState
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.forgot_password_banner),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(200.dp)
                                    .width(300.dp)
                                    .padding(bottom = 16.dp, top = 32.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = formState.email,
                                onValueChange = {
                                    onAction(ForgotPasswordAction.UpdateEmail(it))
                                },
                                label = { Text("Email") },
                                isError = formState.emailError != null,
                                supportingText = {
                                    if (formState.emailError != null) {
                                        Text(formState.emailError)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true
                            )
                        }

                        item {

                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onAction(ForgotPasswordAction.SubmitForgotPassword)
                                },
                                enabled = formState.isValid,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Forgot Password",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                AuthState.Loading -> {
                    showTopBar = false
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is AuthState.Success -> {
                    // We don't need to show anything here as LaunchedEffect
                    // will handle navigation
                }


                else -> { // do nothing for other AuthStates
                }
            }
        }
    }

}

@PreviewLightDark
@Composable
fun ForgotPasswordScreenPreview() {
    MviComposeDemoTheme {
        val previewState = ForgotPasswordState(
            email = "user@example.com",
            password = "",
            emailError = null,
            passwordError = null,
            isValid = true
        )

        Surface {
            ForgotPasswordScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                uiEffect = emptyFlow(),
                onNavigateBack = {}
            )
        }
    }
}

// Empty state preview
@PreviewLightDark
@Composable
fun ForgotPasswordScreenEmptyPreview() {
    MviComposeDemoTheme {
        val previewState = ForgotPasswordState(
            email = "",
            password = "",
            emailError = null,
            passwordError = null,
            isValid = true
        )

        Surface {
            ForgotPasswordScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                uiEffect = emptyFlow(),
                onNavigateBack = {}
            )
        }
    }
}

// Error state preview
@PreviewLightDark
@Composable
fun ForgotPasswordScreenErrorPreview() {
    MviComposeDemoTheme {
        val previewState = ForgotPasswordState(
            email = "jo",
            password = "",
            emailError = "Username must be at least 3 characters",
            passwordError = "",
            isValid = false
        )

        Surface {
            ForgotPasswordScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                uiEffect = emptyFlow(),
                onNavigateBack = {}
            )
        }
    }
}

// Loading state preview
@PreviewLightDark
@Composable
fun ForgotPasswordScreenLoadingPreview() {
    MviComposeDemoTheme {
        Surface {
            ForgotPasswordScreen(
                state = AuthState.Loading,
                onAction = { /* Preview, no action needed */ },
                uiEffect = emptyFlow(),
                onNavigateBack = {}
            )
        }
    }
}