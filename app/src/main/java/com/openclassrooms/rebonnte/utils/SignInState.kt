package com.openclassrooms.rebonnte.utils

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val userData: Userdata? = null
)