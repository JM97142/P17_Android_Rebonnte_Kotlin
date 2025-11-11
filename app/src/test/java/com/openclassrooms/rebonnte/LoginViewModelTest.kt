package com.openclassrooms.rebonnte

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.utils.UiEvent
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var auth: FirebaseAuth

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        auth = mockk(relaxed = true)
        viewModel = LoginViewModel(auth)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Navigation entre étapes
    @Test
    fun onNextStep_with_valid_email_moves_to_step_2() = runTest {
        viewModel.onEmailChange("test@example.com")
        viewModel.onNextStep()
        assertEquals(2, viewModel.uiState.value.currentStep)
        assertNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun onNextStep_with_invalid_email_sets_error() = runTest {
        viewModel.onEmailChange("invalidemail")
        viewModel.onNextStep()
        assertEquals("Email not valid", viewModel.uiState.value.emailError)
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    @Test
    fun onPreviousStep_from_step_2_goes_back_to_step_1() = runTest {
        viewModel.onEmailChange("ok@example.com")
        viewModel.onNextStep()
        viewModel.onPreviousStep()
        assertEquals(1, viewModel.uiState.value.currentStep)
    }

    // Gestion du mot de passe
    @Test
    fun togglePasswordVisibility_changes_visibility_flag() {
        val initial = viewModel.uiState.value.passwordVisible
        viewModel.togglePasswordVisibility()
        assertEquals(!initial, viewModel.uiState.value.passwordVisible)
    }

    // Authentification
    @Test
    fun onSignIn_with_invalid_email_sets_error_and_does_not_call_firebase() = runTest {
        viewModel.onEmailChange("invalid")
        viewModel.onPasswordChange("password123")

        viewModel.onSignIn()

        assertEquals("Email not valid", viewModel.uiState.value.emailError)
        coVerify(exactly = 0) { auth.signInWithEmailAndPassword(any(), any()) }
    }

    @Test
    fun onSignIn_with_empty_password_sets_error() = runTest {
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("")

        viewModel.onSignIn()

        assertEquals("Password cannot be empty", viewModel.uiState.value.passwordError)
    }

    @Test
    fun onSignIn_failure_emits_showMessage_event() = runTest {
        // Arrange
        viewModel.onEmailChange("user@example.com")
        viewModel.onPasswordChange("wrongpass")

        // Simuler échec de connexion
        coEvery { auth.signInWithEmailAndPassword(any(), any()) } coAnswers {
            throw Exception("Invalid credentials")
        }

        // Act
        viewModel.onSignIn()
        advanceUntilIdle()

        // Assert
        val event = viewModel.uiEvent.value
        assertTrue(event is UiEvent.ShowMessage)
        assertTrue((event as UiEvent.ShowMessage).message.contains("Authentication failed"))
        assertFalse(viewModel.uiState.value.isLoading)
    }
}