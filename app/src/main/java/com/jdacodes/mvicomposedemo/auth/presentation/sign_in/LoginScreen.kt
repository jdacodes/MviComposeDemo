package com.jdacodes.mvicomposedemo.auth.presentation.sign_in

import SignInButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.jdacodes.mvicomposedemo.BuildConfig
import com.jdacodes.mvicomposedemo.R
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.composable.HorizontalDividerWithText
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import com.jdacodes.mvicomposedemo.auth.presentation.states.LoginState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    state: AuthState,
    uiEffect: Flow<LoginUiEffect>,
    onAction: (LoginAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    FacebookSdk.setApplicationId(BuildConfig.APP_ID_FACEBOOK)
    FacebookSdk.setClientToken(BuildConfig.CLIENT_TOKEN_FACEBOOK)
    FacebookSdk.sdkInitialize(context)
    val loginManager = LoginManager.getInstance()
    val callbackManager = remember {
        CallbackManager.Factory.create()
    }
    val launcher = rememberLauncherForActivityResult(
        loginManager.createLogInActivityResultContract(callbackManager, null)
    ) {
        // This is handled in FacebookCallback
    }
    val coroutineScope = rememberCoroutineScope()
    var showTopBar by remember { mutableStateOf(true) }

    // Handle UI effects
    LaunchedEffect(true) {
        uiEffect.collect { effect ->
            when (effect) {
                is LoginUiEffect.ShowToast -> {
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
    // Handle one-time navigation events
    LaunchedEffect(state) {
        when (state) {
            is AuthState.Success -> {
                onAction(LoginAction.NavigateToHome)
            }

            is AuthState.Error -> {

            }

            else -> { /* do nothing */
            }
        }
    }
    DisposableEffect(Unit) {
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() {
                // do nothing
            }

            override fun onError(error: FacebookException) {
                // handle error
            }

            override fun onSuccess(result: LoginResult) {
                coroutineScope.launch {
                    val token = result.accessToken
//                    authenticationManager.signInWithFacebook(token)
                    onAction(LoginAction.SignInWithFacebook(token))
                }
            }
        })

        onDispose {
            loginManager.unregisterCallback(callbackManager)
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
                        text = "Login",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Fill the form to access your account",
                        style = MaterialTheme.typography.titleMedium,
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
                is LoginState -> {
                    val formState = state as LoginState
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Image(
                                imageVector = ImageVector.vectorResource(R.drawable.login_banner),
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
                                    onAction(LoginAction.UpdateEmail(it))
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
                                    onAction(LoginAction.UpdatePassword(it))
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
                                    imeAction = ImeAction.Done
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

                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = false, onCheckedChange = {
                                        // TODO: Implement remember me
                                    })
                                    Text(
                                        text = "Remember me",
                                        fontSize = 12.sp,
//                                fontFamily = fontFamily,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    )
                                }
                                TextButton(onClick = {
                                    onAction(LoginAction.NavigateToForgotPassword)
                                }) {
                                    Text(
                                        text = "Forgot password?",
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                            }
                        }
                        item {
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { onAction(LoginAction.SubmitLogin(context)) },
                                enabled = formState.isValid,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),

                                ) {
                                Text(
                                    text = "Login",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                        item {
                            HorizontalDividerWithText()
                            Spacer(Modifier.height(8.dp))
                            SignInButton(
                                icon = Icons.Filled.Mail,
                                buttonString = "Sign up with Email",
                                onClick = { onAction(LoginAction.NavigateToSignUp) },
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            SignInButton(
                                icon = ImageVector.vectorResource(R.drawable.ic_google),
                                buttonString = "Sign in with Google",
                                onClick = {
                                    onAction(LoginAction.SignInWithGoogle)
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                iconSize = 18.dp
                            )
                            Spacer(Modifier.height(16.dp))
                            SignInButton(
                                icon = ImageVector.vectorResource(R.drawable.ic_facebook),
                                buttonString = "Sign in with Facebook",
                                onClick = {
                                    launcher.launch(listOf("email", "public_profile"))
                                },
                                modifier = Modifier
                                    .fillMaxWidth(),
                                iconSize = 24.dp
                            )
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
                        onAction(LoginAction.UpdateEmail(""))
                    }
                }

                else -> { // do nothing for other AuthStates

                }
            }
        }
    }
}

//@PreviewLightDark
//@Composable
//fun LoginScreenPreview() {
//    MviComposeDemoTheme {
//        val previewState = LoginState(
//            email = "user@example.com",
//            password = "password123",
//            emailError = null,
//            passwordError = null,
//            isValid = true
//        )
//
//        Surface {
//            LoginScreen(
//                state = previewState,
//                onAction = { /* Preview, no action needed */ },
//
//                )
//        }
//    }
//}
//
//// Empty state preview
//@PreviewLightDark
//@Composable
//fun LoginScreenEmptyPreview() {
//    MviComposeDemoTheme {
//        val previewState = LoginState(
//            email = "",
//            password = "",
//            emailError = null,
//            passwordError = null,
//            isValid = false
//        )
//
//        Surface {
//            LoginScreen(
//                state = previewState,
//                onAction = { /* Preview, no action needed */ },
//
//                )
//        }
//    }
//}
//
//// Error state preview
//@PreviewLightDark
//@Composable
//fun LoginScreenErrorPreview() {
//    MviComposeDemoTheme {
//        val previewState = LoginState(
//            email = "invalid@email",
//            password = "123",
//            emailError = "Invalid email format",
//            passwordError = "Password must be at least 8 characters",
//            isValid = false
//        )
//
//        Surface {
//            LoginScreen(
//                state = previewState,
//                onAction = { /* Preview, no action needed */ },
//
//                )
//        }
//    }
//}
//
//// Loading state preview
//@PreviewLightDark
//@Composable
//fun LoginScreenLoadingPreview() {
//    MviComposeDemoTheme {
//        Surface {
//            LoginScreen(
//                state = AuthState.Loading,
//                onAction = { /* Preview, no action needed */ },
//
//                )
//        }
//    }
//}