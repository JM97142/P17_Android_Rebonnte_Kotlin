package com.openclassrooms.rebonnte.ui.signin

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import com.openclassrooms.rebonnte.utils.SignInResult
import com.openclassrooms.rebonnte.utils.SignInState
import com.openclassrooms.rebonnte.utils.Userdata
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignInViewModel(
    googleAuthUiClient: GoogleAuthClient,
    emailAuthClient: EmailAuthClient
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    init {
        val signedInUser = googleAuthUiClient.getSignedInUser() ?: emailAuthClient.getSignedInUser()
        if (signedInUser != null) {
            _state.update { it.copy(userData = signedInUser, isSignInSuccessful = true) }
        }
    }

    fun onSignInResult(result: SignInResult) {
        _state.update { currentState ->
            currentState.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
                userData = result.data
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    fun getUserData(): Userdata? = _state.value.userData

    fun onSignInSuccess(userData: Userdata) {
        _state.update { it.copy(userData = userData, isSignInSuccessful = true) }
    }
}