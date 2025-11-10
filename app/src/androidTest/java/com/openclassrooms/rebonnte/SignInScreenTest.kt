package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.openclassrooms.rebonnte.ui.signin.SignInScreen
import com.openclassrooms.rebonnte.utils.SignInState
import org.junit.Rule
import org.junit.Test

/**
 * Tests instrumentés pour SignInScreen (Compose)
 */
class SignInScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displays_app_name_and_login_button() {
        composeTestRule.setContent {
            SignInScreen(
                state = SignInState(),
                onGoogleSignInClick = {},
                onEmailSignInClick = {}
            )
        }

        // Vérifie que le nom de l'app s'affiche
        composeTestRule.onNodeWithText("Rebonnte").assertIsDisplayed()

        // Vérifie que le bouton "Log in" existe
        composeTestRule.onNodeWithText("Log in").assertIsDisplayed()
    }

    @Test
    fun clicking_login_button_triggers_callback() {
        var emailClickCalled = false

        composeTestRule.setContent {
            SignInScreen(
                state = SignInState(),
                onGoogleSignInClick = {},
                onEmailSignInClick = { emailClickCalled = true }
            )
        }

        // Clique sur le bouton
        composeTestRule.onNodeWithText("Log in").performClick()

        // Vérifie que le callback a été déclenché
        assert(emailClickCalled)
    }

    @Test
    fun shows_toast_when_signInError_exists() {
        val errorMessage = "Authentication failed"

        composeTestRule.setContent {
            SignInScreen(
                state = SignInState(signInError = errorMessage),
                onGoogleSignInClick = {},
                onEmailSignInClick = {}
            )
        }

        // Vérifie qu'on ne crash pas et qu'on affiche toujours l'écran
        composeTestRule.onNodeWithText("Log in").assertExists()
    }
}