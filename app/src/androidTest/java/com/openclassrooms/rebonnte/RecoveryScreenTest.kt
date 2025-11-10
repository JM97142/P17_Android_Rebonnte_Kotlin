package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.openclassrooms.rebonnte.ui.recovery.composables.PasswordRecoveryContent
import org.junit.Rule
import org.junit.Test

class RecoveryScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun initial_state_displaysProperElements() {
        composeRule.setContent {
            PasswordRecoveryContent(
                email = "",
                emailError = null,
                isLoading = false,
                showDialog = false,
                onEmailChange = {},
                onSendResetEmail = {},
                onDismissDialog = {},
                padding = PaddingValues()
            )
        }

        composeRule.onNodeWithTag("RecoveryEmailField").assertExists()
        composeRule.onNodeWithTag("RecoverySendButton").assertExists()
        composeRule.onNodeWithText("Send Reset Email").assertExists()
        composeRule.onNodeWithText("Password Recovery").assertDoesNotExist() // Pas le dialog encore
    }

    @Test
    fun typing_email_updatesField() {
        var emailValue = ""

        composeRule.setContent {
            PasswordRecoveryContent(
                email = emailValue,
                emailError = null,
                isLoading = false,
                showDialog = false,
                onEmailChange = { emailValue = it },
                onSendResetEmail = {},
                onDismissDialog = {},
                padding = PaddingValues()
            )
        }

        composeRule.onNodeWithTag("RecoveryEmailField").performTextReplacement("test@example.com")

        composeRule.runOnIdle {
            assert(emailValue == "test@example.com")
        }
    }

    @Test
    fun clicking_send_showsLoadingState() {
        val isLoading = mutableStateOf(false)

        composeRule.setContent {
            PasswordRecoveryContent(
                email = "user@mail.com",
                emailError = null,
                isLoading = isLoading.value,
                showDialog = false,
                onEmailChange = {},
                onSendResetEmail = { isLoading.value = true },
                onDismissDialog = {},
                padding = PaddingValues()
            )
        }

        // Vérifie l'état initial
        composeRule.onNodeWithText("Send Reset Email").assertExists()

        // Clique sur le bouton
        composeRule.onNodeWithTag("RecoverySendButton").performClick()

        // Attendre la recomposition
        composeRule.waitForIdle()

        // Vérifie que le texte a changé
        composeRule.onNodeWithText("Sending...").assertExists()
    }

    @Test
    fun dialog_showsAnd_dismissesProperly() {
        var dialogVisible = true
        var dismissed = false

        composeRule.setContent {
            PasswordRecoveryContent(
                email = "test@domain.com",
                emailError = null,
                isLoading = false,
                showDialog = dialogVisible,
                onEmailChange = {},
                onSendResetEmail = {},
                onDismissDialog = { dismissed = true },
                padding = PaddingValues()
            )
        }

        // Vérifie que le dialog est affiché
        composeRule.onNodeWithText("Password Reset").assertExists()

        // Clic sur OK
        composeRule.onNodeWithTag("RecoveryDialogOkButton").performClick()

        composeRule.runOnIdle {
            assert(dismissed)
        }
    }

    @Test
    fun errorMessage_isDisplayed_whenEmailErrorNotNull() {
        composeRule.setContent {
            PasswordRecoveryContent(
                email = "",
                emailError = "Email cannot be empty",
                isLoading = false,
                showDialog = false,
                onEmailChange = {},
                onSendResetEmail = {},
                onDismissDialog = {},
                padding = PaddingValues()
            )
        }

        composeRule.onNodeWithText("Email cannot be empty").assertExists()
    }
}