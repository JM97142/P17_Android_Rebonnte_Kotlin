package com.openclassrooms.rebonnte

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavController
import io.mockk.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.openclassrooms.rebonnte.ui.login.LogInScreen
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.utils.LoginUiState
import com.openclassrooms.rebonnte.utils.UiEvent
import kotlinx.coroutines.flow.MutableStateFlow

class LogInScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: LoginViewModel
    private lateinit var navController: NavController

    private val uiStateFlow = MutableStateFlow(LoginUiState())
    private val uiEventFlow = MutableStateFlow<UiEvent?>(null)

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true) {
            every { uiState } returns uiStateFlow
            every { uiEvent } returns uiEventFlow
        }
        navController = mockk(relaxed = true)
    }

    @Test
    fun displays_email_step_initially() {
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("emailTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("nextButton").assertIsDisplayed()
    }

    @Test
    fun next_button_moves_to_password_step_if_email_valid() {
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("emailTextField").performTextInput("user@example.com")
        composeTestRule.onNodeWithTag("nextButton").performClick()

        // Simule que le ViewModel met à jour le StateFlow
        uiStateFlow.value = uiStateFlow.value.copy(currentStep = 2)

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("passwordTextField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("signInButton").assertIsDisplayed()
        composeTestRule.onNodeWithTag("togglePasswordVisibility").assertIsDisplayed()
        composeTestRule.onNodeWithTag("forgotPasswordButton").assertIsDisplayed()
    }

    @Test
    fun shows_error_if_email_empty() {
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("nextButton").performClick()

        verify { viewModel.onNextStep() }
    }

    @Test
    fun can_toggle_password_visibility() {
        // Étape mot de passe
        uiStateFlow.value = uiStateFlow.value.copy(currentStep = 2)
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("togglePasswordVisibility").performClick()
        verify { viewModel.togglePasswordVisibility() }
    }

    @Test
    fun sign_in_button_triggers_onSignIn() {
        uiStateFlow.value = uiStateFlow.value.copy(currentStep = 2)
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("signInButton").performClick()
        verify { viewModel.onSignIn() }
    }

    @Test
    fun forgot_password_button_triggers_navigation() {
        uiStateFlow.value = uiStateFlow.value.copy(currentStep = 2)
        composeTestRule.setContent {
            LogInScreen(navController = navController, viewModel = viewModel)
        }

        composeTestRule.onNodeWithTag("forgotPasswordButton").performClick()
        verify { navController.navigate("password_recovery") }
    }
}