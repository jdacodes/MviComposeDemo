package com.jdacodes.mvicomposedemo

import com.jdacodes.mvicomposedemo.auth.domain.model.User
import com.jdacodes.mvicomposedemo.auth.domain.repository.AuthRepository
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginAction
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginViewModel
import com.jdacodes.mvicomposedemo.navigation.util.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.jdacodes.mvicomposedemo.auth.presentation.sign_in.LoginUiEffect
import com.jdacodes.mvicomposedemo.auth.presentation.states.AuthState
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val authRepository: AuthRepository = mock()
    private val navigator: Navigator = mock()
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setup() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private lateinit var viewModel: LoginViewModel

    @Test
    fun `signInWithGoogle - successful login - emits success state and shows toast`() = runTest {
        val expectedUser = User("123", "test@example.com", "Test User")
        whenever(authRepository.signInWithGoogle()).thenReturn(expectedUser)

        viewModel = LoginViewModel(authRepository, navigator)

        viewModel.state.test {
            val formState = awaitItem()
            assertThat(formState).isInstanceOf(AuthState.Form::class.java)

            viewModel.onAction(LoginAction.SignInWithGoogle)
            
            testDispatcher.scheduler.runCurrent()
            assertThat(awaitItem()).isEqualTo(AuthState.Loading)
            
            testDispatcher.scheduler.advanceUntilIdle()
            assertThat(awaitItem() as AuthState.Success).isEqualTo(AuthState.Success(expectedUser))
            cancelAndIgnoreRemainingEvents()
        }

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.uiEffect.test {
            assertThat(awaitItem()).isEqualTo(LoginUiEffect.ShowToast("Login successful!"))
            cancelAndIgnoreRemainingEvents()
        }
    }
}