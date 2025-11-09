package com.openclassrooms.rebonnte.ui.nav

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.openclassrooms.rebonnte.ui.aisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.login.EmailScreen
import com.openclassrooms.rebonnte.ui.login.LogInScreen
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.login.RecoveryScreen
import com.openclassrooms.rebonnte.ui.medicine.AddNewMedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.signin.SignInScreen
import com.openclassrooms.rebonnte.ui.signin.SignInViewModel
import com.openclassrooms.rebonnte.ui.signup.SignUpScreen
import com.openclassrooms.rebonnte.ui.signup.SignUpViewModel
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    aisleViewModel: AisleViewModel,
    medicineViewModel: MedicineViewModel,
    googleAuthClient: GoogleAuthClient,
    emailAuthClient: EmailAuthClient,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "sign_in",
        modifier = modifier
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
            val lifecycleScope = rememberCoroutineScope()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        lifecycleScope.launch {
                            val res = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                            viewModel.onSignInResult(res)
                            if (res.data != null) navController.navigate("aisle") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    }
                }
            )

            LaunchedEffect(Unit) {
                googleAuthClient.signIn()?.let { launcher.launch(IntentSenderRequest.Builder(it).build()) }
            }
        }

        composable("login") {
            LogInScreen(
                navController = navController,
                viewModel = loginViewModel)
        }

        composable("email_sign_in") {
            EmailScreen(
                onLogInClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") },
                navController = navController)
        }

        composable("password_recovery") { RecoveryScreen(navController) }

        composable("signup") { SignUpScreen(
            onLoginSuccess = { navController.navigate("aisle") { popUpTo("signup") { inclusive = true } } },
            navController = navController
        ) }

        // Aisle & Medicine Screen
        composable("aisle") { AisleScreen(navController, aisleViewModel) }
        composable("aisle_detail/{aisleName}", arguments = listOf(navArgument("aisleName") { type = NavType.StringType })) { backStackEntry ->
            val aisleName = backStackEntry.arguments?.getString("aisleName") ?: "Unknown"
            AisleDetailScreen(aisleName, medicineViewModel, navController)
        }

        composable("medicine") { MedicineScreen(navController, medicineViewModel) }
        composable("medicine_detail/{medicineName}", arguments = listOf(navArgument("medicineName") { type = NavType.StringType })) { backStackEntry ->
            val medicineName = backStackEntry.arguments?.getString("medicineName") ?: "Unknown"
            MedicineDetailScreen(medicineName, medicineViewModel) { navController.popBackStack() }
        }

        composable("add_medicine") { AddNewMedicineScreen(navController, medicineViewModel, aisleViewModel) }
    }
}