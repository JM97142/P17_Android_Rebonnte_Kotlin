package com.openclassrooms.rebonnte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.openclassrooms.rebonnte.ui.signup.SignUpEvent
import com.openclassrooms.rebonnte.ui.signup.SignUpScreen
import com.openclassrooms.rebonnte.ui.signup.SignUpUiState
import com.openclassrooms.rebonnte.ui.signup.SignUpViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SignUpScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun fullSignUpFlow_interactions_triggerNavigation() {
        // --- Arrange: flows contrôlables ---
        val uiStateFlow = MutableStateFlow(
            SignUpUiState(
                currentStep = 1,
                email = "",
                name = "",
                lastname = "",
                password = ""
            )
        )
        val eventFlow = MutableStateFlow<SignUpEvent?>(null)

        // --- Mock ViewModel ---
        val mockViewModel = mockk<SignUpViewModel>(relaxUnitFun = true)

        // Expose les flows
        every { mockViewModel.uiState } returns uiStateFlow
        every { mockViewModel.event } returns eventFlow

        // Quand on change l'email via onEmailChange, on met à jour uiStateFlow
        every { mockViewModel.onEmailChange(any()) } answers {
            val newEmail = firstArg<String>()
            uiStateFlow.value = uiStateFlow.value.copy(email = newEmail)
        }

        // Simule la réussite et on passe à l'étape 2
        every { mockViewModel.validateEmail() } answers {
            uiStateFlow.value = uiStateFlow.value.copy(currentStep = 2)
        }

        every { mockViewModel.onNameChange(any()) } answers {
            val newName = firstArg<String>()
            uiStateFlow.value = uiStateFlow.value.copy(name = newName)
        }
        every { mockViewModel.onLastnameChange(any()) } answers {
            val newLast = firstArg<String>()
            uiStateFlow.value = uiStateFlow.value.copy(lastname = newLast)
        }

        // goToNextStep : on simule le passage à l'étape 3
        every { mockViewModel.goToNextStep() } answers {
            uiStateFlow.value = uiStateFlow.value.copy(currentStep = 3)
        }

        every { mockViewModel.onPasswordChange(any()) } answers {
            val newPwd = firstArg<String>()
            uiStateFlow.value = uiStateFlow.value.copy(password = newPwd)
        }

        // validatePassword : on simule l'émission de l'événement NavigateToAisle
        every { mockViewModel.validatePassword() } answers {
            eventFlow.value = SignUpEvent.NavigateToAisle
        }

        var navigated = false

        // Set content
        composeTestRule.setContent {
            SignUpScreen(
                onLoginSuccess = { navigated = true },
                navController = mockk(relaxed = true),
                viewModel = mockViewModel
            )
        }
        // Step 1 : email
        composeTestRule.onNodeWithTag("EmailField")
            .assertExists()
            .performTextReplacement("user@example.com")

        composeTestRule.onNodeWithTag("EmailNextButton")
            .assertExists()
            .performClick()

        composeTestRule.waitForIdle()

        verify { mockViewModel.onEmailChange("user@example.com") }
        verify { mockViewModel.validateEmail() }
        // Step 2 : name / lastname
        composeTestRule.onNodeWithTag("NameField")
            .assertExists()
            .performTextReplacement("John")

        composeTestRule.onNodeWithTag("LastNameField")
            .assertExists()
            .performTextReplacement("Doe")

        composeTestRule.onNodeWithTag("NameNextButton")
            .assertExists()
            .performClick()

        composeTestRule.waitForIdle()

        verify { mockViewModel.onNameChange("John") }
        verify { mockViewModel.onLastnameChange("Doe") }
        verify { mockViewModel.goToNextStep() }
        // Step 3 : password
        composeTestRule.onNodeWithTag("PasswordField")
            .assertExists()
            .performTextReplacement("123456")

        composeTestRule.onNodeWithTag("PasswordLoginButton")
            .assertExists()
            .performClick()

        composeTestRule.waitForIdle()

        verify { mockViewModel.onPasswordChange("123456") }
        verify { mockViewModel.validatePassword() }

        assert(navigated) { "Navigation vers 'aisle' n'a pas été déclenchée" }
    }
}