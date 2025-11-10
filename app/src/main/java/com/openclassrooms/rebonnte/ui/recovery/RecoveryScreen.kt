package com.openclassrooms.rebonnte.ui.recovery

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.ui.recovery.composables.PasswordRecoveryContent
import com.openclassrooms.rebonnte.ui.recovery.composables.PasswordRecoveryTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecoveryScreen(
    navController: NavController,
    viewModel: RecoveryViewModel = koinViewModel()
) {
    val email by viewModel.email.collectAsState()
    val emailError by viewModel.emailError.collectAsState()
    val isSuccess by viewModel.isSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = { PasswordRecoveryTopBar(navController) }
    ) { padding ->
        PasswordRecoveryContent(
            email = email,
            emailError = emailError,
            isLoading = isLoading,
            showDialog = isSuccess,
            onEmailChange = viewModel::onEmailChange,
            onSendResetEmail = viewModel::sendResetEmail,
            onDismissDialog = {
                viewModel.resetDialog()
                navController.navigateUp()
            },
            padding = padding
        )
    }
}