package com.openclassrooms.rebonnte.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.utils.LoginUiState
import com.openclassrooms.rebonnte.utils.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    private val _uiEvent = MutableStateFlow<UiEvent?>(null)
    val uiEvent: StateFlow<UiEvent?> = _uiEvent

    fun onNextStep() {
        val error = validateEmail(_uiState.value.email)
        _uiState.value = if (error == null) {
            _uiState.value.copy(emailError = null, currentStep = 2)
        } else {
            _uiState.value.copy(emailError = error)
        }
    }

    fun onPreviousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.value = _uiState.value.copy(currentStep = _uiState.value.currentStep - 1)
        }
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
    }

    fun onSignIn() {
        val email = _uiState.value.email
        val password = _uiState.value.password

        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)

        if (emailError != null || passwordError != null) {
            _uiState.value = _uiState.value.copy(emailError = emailError, passwordError = passwordError)
            return
        }

        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _uiEvent.value = UiEvent.Navigate("aisle")
                    } else {
                        _uiEvent.value = UiEvent.ShowMessage("Authentication failed")
                    }
                }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email cannot be empty"
            !email.matches(Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) -> "Email not valid"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? =
        if (password.isBlank()) "Password cannot be empty" else null

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }
}