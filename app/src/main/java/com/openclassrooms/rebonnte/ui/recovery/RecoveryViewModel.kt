package com.openclassrooms.rebonnte.ui.recovery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecoveryViewModel(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _emailError = MutableStateFlow<String?>(null)
    val emailError = _emailError.asStateFlow()

    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _emailError.value = null
    }

    fun sendResetEmail() {
        val emailValue = _email.value.trim()

        if (emailValue.isBlank()) {
            _emailError.value = "Email cannot be empty"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            auth.sendPasswordResetEmail(emailValue)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _isSuccess.value = true
                    } else {
                        val message = task.exception?.message ?: "Unknown error"
                        _emailError.value = when {
                            message.contains("no user record", ignoreCase = true) ->
                                "No account found with this email address."
                            else -> "Failed to send reset email: $message"
                        }
                    }
                }
        }
    }

    fun resetDialog() {
        _isSuccess.value = false
    }
}