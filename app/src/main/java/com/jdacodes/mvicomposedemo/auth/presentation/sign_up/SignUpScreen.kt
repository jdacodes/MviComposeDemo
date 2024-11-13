package com.jdacodes.mvicomposedemo.auth.presentation.sign_up

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.SignUpState
import com.jdacodes.mvicomposedemo.ui.theme.MviComposeDemoTheme

@Composable
fun SignUpScreen(
    state: AuthState,
    onAction: (SignUpAction) -> Unit,
    onSignUpSuccess: () -> Unit, //Navigation callback
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showTopBar by remember { mutableStateOf(true) }

    //Handle one-time events
    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    "Sign-up successful!",
                    Toast.LENGTH_SHORT
                ).show()
                onSignUpSuccess()
            }

            is AuthState.Error -> {
                val error = (state as AuthState.Error).message
                Toast.makeText(
                    context,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> { /* do nothing */
            }
        }
    }
    Scaffold(
        topBar = {
            if (showTopBar){
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    Text(
                        text = "Sign up",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Register to create your account",
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
                is SignUpState -> {
                    val formState = state as SignUpState
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.sign_up_banner),
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
                                    onAction(SignUpAction.UpdateEmail(it))
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
                                    imeAction = ImeAction.Next
                                ),
                                singleLine = true
                            )
                        }
                        item {
                            var passwordVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = formState.password,
                                onValueChange = {
                                    onAction(SignUpAction.UpdatePassword(it))
                                },
                                label = { Text("Password") },
                                isError = formState.passwordError != null,
                                supportingText = {
                                    if (formState.passwordError != null) {
                                        Text(formState.passwordError)
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = if (passwordVisible) {
                                                Icons.Filled.Visibility
                                            } else {
                                                Icons.Filled.VisibilityOff
                                            },
                                            contentDescription = if (passwordVisible) {
                                                "Hide password"
                                            } else {
                                                "Show password"
                                            }
                                        )
                                    }
                                },
                                singleLine = true
                            )
                        }
                        item {
                            var confirmVisible by remember { mutableStateOf(false) }
                            OutlinedTextField(
                                value = formState.confirmPassword,
                                onValueChange = {
                                    onAction(SignUpAction.UpdateConfirmPassword(it))
                                },
                                label = { Text("Confirm Password") },
                                isError = formState.confirmPasswordError != null,
                                supportingText = {
                                    if (formState.confirmPasswordError != null) {
                                        Text(formState.confirmPasswordError)
                                    }
                                },
                                visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Next
                                ),
                                trailingIcon = {
                                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                                        Icon(
                                            imageVector = if (confirmVisible) {
                                                Icons.Filled.Visibility
                                            } else {
                                                Icons.Filled.VisibilityOff
                                            },
                                            contentDescription = if (confirmVisible) {
                                                "Hide password"
                                            } else {
                                                "Show password"
                                            }
                                        )
                                    }
                                },
                                singleLine = true
                            )
                        }
                        item {
                            OutlinedTextField(
                                value = formState.username,
                                onValueChange = {
                                    onAction(SignUpAction.UpdateUsername(it))
                                },
                                label = { Text("Username") },
                                isError = formState.usernameError != null,
                                supportingText = {
                                    if (formState.usernameError != null) {
                                        Text(formState.usernameError)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                singleLine = true
                            )
                        }
                        item {

                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    onAction(SignUpAction.SubmitSignUp)
                                },
                                enabled = formState.isValid,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Sign up",
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

                is AuthState.Error -> {
                    // Error state is handled by LaunchedEffect showing Toast
                    // We can return to form state after showing error
                    LaunchedEffect(Unit) {
                        onAction(SignUpAction.UpdateEmail(""))
                    }
                }

                else -> { // do nothing for other AuthStates
                }
            }
        }
    }

}

@PreviewLightDark
@Composable
fun SignUpScreenPreview() {
    MviComposeDemoTheme {
        val previewState = SignUpState(
            email = "user@example.com",
            password = "password123",
            emailError = null,
            passwordError = null,
            isValid = true
        )

        Surface {
            SignUpScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                onSignUpSuccess = {},
                onNavigateBack = {}
            )
        }
    }
}

// Empty state preview
@PreviewLightDark
@Composable
fun SignUpScreenEmptyPreview() {
    MviComposeDemoTheme {
        val previewState = SignUpState(
            email = "user@example.com",
            password = "password123",
            emailError = null,
            passwordError = null,
            isValid = true
        )

        Surface {
            SignUpScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                onSignUpSuccess = {},
                onNavigateBack = {}
            )
        }
    }
}

// Error state preview
@PreviewLightDark
@Composable
fun SignUpScreenErrorPreview() {
    MviComposeDemoTheme {
        val previewState = SignUpState(
            email = "us",
            password = "pass",
            confirmPassword = "pass1",
            username = "me",
            emailError = "Username must be at least 3 characters",
            passwordError = "Password must be at least 8 characters",
            confirmPasswordError = "Passwords do not match",
            usernameError = "Username must be at least 3 characters",
            isValid = false
        )

        Surface {
            SignUpScreen(
                state = previewState,
                onAction = { /* Preview, no action needed */ },
                onSignUpSuccess = {},
                onNavigateBack = {}
            )
        }
    }
}

// Loading state preview
@PreviewLightDark
@Composable
fun SignUpScreenLoadingPreview() {
    MviComposeDemoTheme {
        Surface {
            SignUpScreen(
                state = AuthState.Loading,
                onAction = { /* Preview, no action needed */ },
                onSignUpSuccess = {},
                onNavigateBack = {}
            )
        }
    }
}