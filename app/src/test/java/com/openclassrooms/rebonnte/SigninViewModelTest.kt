package com.openclassrooms.rebonnte

import com.openclassrooms.rebonnte.ui.signin.SignInViewModel
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import com.openclassrooms.rebonnte.utils.SignInResult
import com.openclassrooms.rebonnte.utils.SignInState
import com.openclassrooms.rebonnte.utils.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class SignInViewModelTest {

    private lateinit var googleAuthClient: GoogleAuthClient
    private lateinit var emailAuthClient: EmailAuthClient
    private lateinit var viewModel: SignInViewModel

    @Before
    fun setUp() {
        googleAuthClient = mock()
        emailAuthClient = mock()
    }

    @Test
    fun `init should set signed-in user from GoogleAuthClient`() = runTest {
        val mockUser = UserData("1", "User Name", "user@gmail.com", null, null)
        whenever(googleAuthClient.getSignedInUser()).thenReturn(mockUser)

        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val state = viewModel.state.first()
        assertEquals(mockUser, state.userData)
        assertTrue(state.isSignInSuccessful)
        assertNull(state.signInError)
    }

    @Test
    fun `init should set signed-in user from EmailAuthClient when Google returns null`() = runTest {
        val mockUser = UserData(
            "1",
            "User Name",
            "user@gmail.com",
            null,
            null
        )
        whenever(googleAuthClient.getSignedInUser()).thenReturn(null)
        whenever(emailAuthClient.getSignedInUser()).thenReturn(mockUser)

        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val state = viewModel.state.first()
        assertEquals(mockUser, state.userData)
        assertTrue(state.isSignInSuccessful)
    }

    @Test
    fun `init should start with empty state when no user signed in`() = runTest {
        whenever(googleAuthClient.getSignedInUser()).thenReturn(null)
        whenever(emailAuthClient.getSignedInUser()).thenReturn(null)

        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val state = viewModel.state.first()
        assertNull(state.userData)
        assertFalse(state.isSignInSuccessful)
        assertNull(state.signInError)
    }

    @Test
    fun `onSignInResult should update state with success`() = runTest {
        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val resultUser = UserData(
            "1",
            "Success User",
            "user@gmail.com",
            null,
            null
        )
        val result = SignInResult(data = resultUser, errorMessage = null)

        viewModel.onSignInResult(result)

        val state = viewModel.state.first()
        assertEquals(resultUser, state.userData)
        assertTrue(state.isSignInSuccessful)
        assertNull(state.signInError)
    }

    @Test
    fun `onSignInResult should update state with error`() = runTest {
        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val result = SignInResult(data = null, errorMessage = "Invalid credentials")

        viewModel.onSignInResult(result)

        val state = viewModel.state.first()
        assertFalse(state.isSignInSuccessful)
        assertEquals("Invalid credentials", state.signInError)
        assertNull(state.userData)
    }

    @Test
    fun `resetState should clear all fields`() = runTest {
        viewModel = SignInViewModel(googleAuthClient, emailAuthClient)

        val resultUser = UserData(
            "1",
            "Reset User",
            "reset@test.com",
            null,
            null
        )
        viewModel.onSignInResult(SignInResult(resultUser, null))

        viewModel.resetState()

        val state = viewModel.state.first()
        assertEquals(SignInState(), state)
    }
}