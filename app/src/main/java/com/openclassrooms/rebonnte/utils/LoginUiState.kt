package com.openclassrooms.rebonnte.utils

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val passwordVisible: Boolean = false,
    val currentStep: Int = 1,
    val isLoading: Boolean = false
)