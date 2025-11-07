package com.openclassrooms.rebonnte.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.login.composables.EmailStep
import com.openclassrooms.rebonnte.ui.login.composables.LoginScreenLogo
import com.openclassrooms.rebonnte.ui.login.composables.LoginTopAppBar
import com.openclassrooms.rebonnte.ui.login.composables.PasswordStep

@Composable
fun LogInScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf<String?>(null) }
    val passwordError = remember { mutableStateOf<String?>(null) }
    val currentStep = remember { mutableStateOf(1) }
    val auth = FirebaseAuth.getInstance()
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            LoginTopAppBar(currentStep, navController)
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

            when (currentStep.value) {
                1 -> EmailStep(
                    email = email,
                    emailError = emailError,
                    onNext = {
                        emailError.value = validateEmail(email.value)
                        if (emailError.value == null) currentStep.value = 2
                    }
                )
                2 -> PasswordStep(
                    email = email,
                    password = password,
                    passwordError = passwordError,
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
                    onSignIn = {
                        passwordError.value = validatePassword(password.value)
                        if (emailError.value == null && passwordError.value == null) {
                            signInWithEmail(auth, email.value, password.value, navController)
                        }
                    },
                    onForgotPassword = { navController.navigate("password_recovery") }
                )
            }
        }
    }
}

fun validateEmail(email: String): String? {
    return when {
        email.isBlank() -> "Email cannot be empty"
        !isValidEmail(email) -> "Email not valid"
        else -> null
    }
}

fun validatePassword(password: String): String? {
    return if (password.isBlank()) "Password cannot be empty" else null
}

fun signInWithEmail(auth: FirebaseAuth, email: String, password: String, navController: NavController) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate("aisle") {
                    popUpTo(0) { inclusive = true }
                }
                Toast.makeText(navController.context, "Sign in successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(navController.context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}