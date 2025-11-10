package com.openclassrooms.rebonnte.ui.signup

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.signup.composables.EmailInput
import com.openclassrooms.rebonnte.ui.signup.composables.NameLastnameInput
import com.openclassrooms.rebonnte.ui.signup.composables.PasswordInput
import com.openclassrooms.rebonnte.ui.signup.composables.SignUpTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(
    onLoginSuccess: () -> Unit,
    navController: NavController,
    viewModel: SignUpViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val event by viewModel.event.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Gère les événements de navigation ou messages
    LaunchedEffect(event) {
        when (val e = event) {
            is SignUpEvent.ShowMessage -> {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
            is SignUpEvent.NavigateToAisle -> onLoginSuccess()
            null -> {}
        }
        viewModel.clearEvent()
    }

    Scaffold(
        topBar = {
            SignUpTopBar(
                currentStep = uiState.currentStep,
                onBack = { handleBack(navController, viewModel) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayAppIcon()

            Spacer(modifier = Modifier.height(21.dp))

            when (uiState.currentStep) {
                1 -> EmailInput(
                    email = uiState.email,
                    emailError = uiState.emailError,
                    onEmailChange = viewModel::onEmailChange,
                    onValidateEmail = { viewModel.validateEmail() }
                )

                2 -> NameLastnameInput(
                    name = uiState.name,
                    lastname = uiState.lastname,
                    onNameChange = viewModel::onNameChange,
                    onLastnameChange = viewModel::onLastnameChange,
                    onNext = { viewModel.goToNextStep() }
                )

                3 -> PasswordInput(
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                    onLogin = { viewModel.validatePassword() }
                )
            }

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(20.dp))
                CircularProgressIndicator(
                    modifier = Modifier.semantics { contentDescription = "Chargement en cours" }
                )
            }
        }
    }
}

@Composable
fun DisplayAppIcon() {
    Text(
        text = stringResource(id = R.string.app_name),
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp,
        modifier = Modifier.semantics {
            heading()
            contentDescription = "Nom de l'application"
        }
    )
}

private fun handleBack(navController: NavController, viewModel: SignUpViewModel) {
    val currentStep = viewModel.uiState.value.currentStep
    if (currentStep > 1) {
        viewModel.goToPreviousStep()
    } else {
        navController.navigate("sign_in")
    }
}