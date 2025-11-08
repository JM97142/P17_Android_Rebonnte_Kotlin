package com.openclassrooms.rebonnte.utils

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userData: com.openclassrooms.rebonnte.utils.UserData? = null
)