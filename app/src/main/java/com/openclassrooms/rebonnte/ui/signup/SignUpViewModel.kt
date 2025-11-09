package com.openclassrooms.rebonnte.ui.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SignUpUiState(
    val email: String = "",
    val name: String = "",
    val lastname: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val currentStep: Int = 1,
    val isLoading: Boolean = false
)

sealed class SignUpEvent {
    data class ShowMessage(val message: String) : SignUpEvent()
    object NavigateToAisle : SignUpEvent()
}

class SignUpViewModel(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    private val _event = MutableStateFlow<SignUpEvent?>(null)
    val event: StateFlow<SignUpEvent?> = _event

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(name = value)
    }

    fun onLastnameChange(value: String) {
        _uiState.value = _uiState.value.copy(lastname = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun validateEmail() {
        val email = _uiState.value.email
        val emailError = when {
            email.isBlank() -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Please enter a valid email address"
            else -> null
        }

        if (emailError != null) {
            _uiState.value = _uiState.value.copy(emailError = emailError)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    val isExistingUser = task.result?.signInMethods?.isNotEmpty() == true
                    _uiState.value = _uiState.value.copy(
                        currentStep = if (isExistingUser) 3 else 2,
                        emailError = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        emailError = "Error: ${task.exception?.message}"
                    )
                }
            }
    }

    fun validatePassword() {
        val state = _uiState.value
        val passwordError = when {
            state.password.isBlank() -> "Password cannot be empty"
            state.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }

        if (passwordError != null) {
            _uiState.value = _uiState.value.copy(passwordError = passwordError)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore()
                } else {
                    signInExistingUser()
                }
            }
    }

    private fun saveUserToFirestore() {
        val user = auth.currentUser ?: return
        val state = _uiState.value
        val data = mapOf(
            "firstName" to state.name,
            "lastName" to state.lastname
        )

        firestore.collection("users").document(user.uid)
            .set(data)
            .addOnSuccessListener {
                _event.value = SignUpEvent.ShowMessage("Registration successful")
                _event.value = SignUpEvent.NavigateToAisle
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
            .addOnFailureListener {
                _event.value = SignUpEvent.ShowMessage("Failed to save user data: ${it.message}")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
    }

    private fun signInExistingUser() {
        val state = _uiState.value
        auth.signInWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    _event.value = SignUpEvent.ShowMessage("Login successful")
                    _event.value = SignUpEvent.NavigateToAisle
                } else {
                    _uiState.value = _uiState.value.copy(
                        passwordError = "Error: ${task.exception?.message}"
                    )
                }
            }
    }

    fun goToNextStep() {
        _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep + 1)
    }

    fun goToPreviousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun clearEvent() {
        _event.value = null
    }
}