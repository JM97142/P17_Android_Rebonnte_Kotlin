package com.openclassrooms.rebonnte.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.ui.login.composables.EmailStep
import com.openclassrooms.rebonnte.ui.login.composables.LoginScreenLogo
import com.openclassrooms.rebonnte.ui.login.composables.LoginTopAppBar
import com.openclassrooms.rebonnte.ui.login.composables.PasswordStep
import com.openclassrooms.rebonnte.utils.UiEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun LogInScreen(
    navController: NavController,
    viewModel: LoginViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState(initial = null)
    val scrollState = rememberScrollState()

    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is UiEvent.Navigate -> navController.navigate(event.route) {
                popUpTo(0) { inclusive = true }
            }
            is UiEvent.ShowMessage ->
                Toast.makeText(navController.context, event.message, Toast.LENGTH_SHORT).show()
            null -> Unit
        }
    }

    Scaffold(
        topBar = {
            LoginTopAppBar(
                currentStep = uiState.currentStep,
                onBackClick = {
                    if (uiState.currentStep > 1) {
                        viewModel.onPreviousStep()
                    } else {
                        navController.navigate("email_sign_in")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginScreenLogo()

            when (uiState.currentStep) {
                1 -> EmailStep(
                    email = uiState.email,
                    emailError = uiState.emailError,
                    onEmailChange = viewModel::onEmailChange,
                    onNext = viewModel::onNextStep
                )
                2 -> PasswordStep(
                    email = uiState.email,
                    password = uiState.password,
                    passwordError = uiState.passwordError,
                    passwordVisible = uiState.passwordVisible,
                    onPasswordChange = viewModel::onPasswordChange,
                    onPasswordVisibilityToggle = viewModel::togglePasswordVisibility,
                    onSignIn = viewModel::onSignIn,
                    onForgotPassword = { navController.navigate("password_recovery") }
                )
            }
        }
    }
}