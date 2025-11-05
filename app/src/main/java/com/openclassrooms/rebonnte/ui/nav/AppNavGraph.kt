package com.openclassrooms.rebonnte.ui.nav

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.login.EmailScreen
import com.openclassrooms.rebonnte.ui.login.LogInScreen
import com.openclassrooms.rebonnte.ui.login.RecoveryScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.signin.SignInScreen
import com.openclassrooms.rebonnte.ui.signin.SignInViewModel
import com.openclassrooms.rebonnte.ui.signup.SignUpScreen
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.appNavigation(
    navController: NavController,
    googleAuthClient: GoogleAuthClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope
) {
    composable("sign_in") {
        val viewModel: SignInViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        SignInScreen(
            state = state,
            onGoogleSignInClick = { navController.navigate("google_sign_in") },
            onEmailSignInClick = { navController.navigate("email_sign_in") }
        )
    }

    composable("google_sign_in") {
        val viewModel: SignInViewModel = koinViewModel()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val googleSignInLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
            onResult = { result ->
                if (result.resultCode == RESULT_OK) {
                    lifecycleScope.launch {
                        val signInResult = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                        viewModel.onSignInResult(signInResult)
                        if (signInResult.data != null) {
                            navController.navigate("aisle") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            }
        )

        LaunchedEffect(Unit) {
            googleAuthClient.signIn()?.let { intentSender ->
                googleSignInLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
        }
    }

    composable("email_sign_in") {
        EmailScreen(
            onLogInClick = { navController.navigate("login") },
            onSignUpClick = { navController.navigate("signup") },
            navController = navController
        )
    }

    composable("aisle") {
        val aisleViewModel: AisleViewModel = koinViewModel()
    }

    composable("medicine") {
        val medicineViewModel: MedicineViewModel = koinViewModel()
        MedicineScreen(navController, medicineViewModel)
    }

    composable("signup") {
        SignUpScreen(
            onLoginSuccess = {
                navController.navigate("aisle") {
                    popUpTo("signup") { inclusive = true }
                }
            },
            navController = navController
        )
    }

    composable("login") {
        LogInScreen(navController = navController)
    }

    composable("password_recovery") {
        RecoveryScreen(navController = navController)
    }

    composable(
        "medicine_detail/{medicineName}",
        arguments = listOf(navArgument("medicineName") { type = NavType.StringType })
    ) { backStackEntry ->
        val medicineName = backStackEntry.arguments?.getString("medicineName") ?: "Unknown"
        val medicineViewModel: MedicineViewModel = koinViewModel()
        MedicineDetailScreen(
            name = medicineName,
            viewModel = medicineViewModel,
            onBack = { navController.popBackStack() }
        )
    }
}