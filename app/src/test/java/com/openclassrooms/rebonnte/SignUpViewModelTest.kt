package com.openclassrooms.rebonnte

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.ui.signup.SignUpViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel
    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore

    @Before
    fun setup() {
        mockAuth = mock()
        mockFirestore = mock()
        viewModel = SignUpViewModel(mockAuth, mockFirestore)
    }

    @Test
    fun `blank email sets error`() {
        viewModel.onEmailChange("")
        viewModel.validateEmail()

        assert(viewModel.uiState.value.emailError == "Email cannot be empty")
    }

    @Test
    fun `blank password sets error`() {
        viewModel.onPasswordChange("")
        viewModel.validatePassword()

        assert(viewModel.uiState.value.passwordError == "Password cannot be empty")
    }

    @Test
    fun `short password sets error`() {
        viewModel.onPasswordChange("123")
        viewModel.validatePassword()

        assert(viewModel.uiState.value.passwordError == "Password must be at least 6 characters")
    }

    @Test
    fun `goToNextStep increments currentStep`() {
        val initial = viewModel.uiState.value.currentStep
        viewModel.goToNextStep()

        assert(viewModel.uiState.value.currentStep == initial + 1)
    }

    @Test
    fun `goToPreviousStep decrements currentStep`() {
        viewModel.onEmailChange("test@example.com")
        viewModel.goToNextStep()
        val initial = viewModel.uiState.value.currentStep
        viewModel.goToPreviousStep()

        assert(viewModel.uiState.value.currentStep == initial - 1)
    }

    @Test
    fun `clearEvent sets event to null`() {
        viewModel.clearEvent()
        assert(viewModel.event.value == null)
    }
}