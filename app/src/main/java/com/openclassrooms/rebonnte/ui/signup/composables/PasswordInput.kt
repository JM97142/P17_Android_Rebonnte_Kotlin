package com.openclassrooms.rebonnte.ui.signup.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    val passwordError = remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_password),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabeledPasswordField(
            password = password,
            onPasswordChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onVisibilityToggle = { passwordVisible = !passwordVisible },
            passwordError = passwordError.value
        )

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            password = password,
            passwordError = passwordError,
            context = context,
            onLogin = onLogin
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    passwordError: String?
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(text = stringResource(id = R.string.password)) },
        isError = passwordError != null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("PasswordField")
            .semantics { contentDescription = "Champ mot de passe" },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = onVisibilityToggle,
                modifier = Modifier.semantics {
                    contentDescription = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
                }
            ) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            errorTextColor = MaterialTheme.colorScheme.error,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            containerColor = MaterialTheme.colorScheme.surface
        )
    )

    if (passwordError != null) {
        Text(
            text = passwordError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun LoginButton(
    password: String,
    passwordError: MutableState<String?>,
    context: Context,
    onLogin: () -> Unit
) {
    Button(
        onClick = {
            passwordError.value = validatePassword(password)
            if (passwordError.value == null) {
                Toast.makeText(context, "Proceeding with login", Toast.LENGTH_SHORT).show()
                onLogin()
            } else {
                Toast.makeText(context, "Password validation failed", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp)
            .testTag("PasswordLoginButton")
            .semantics { contentDescription = "Bouton connexion" },
        shape = RectangleShape
    ) {
        Text(text = stringResource(id = R.string.save), color = MaterialTheme.colorScheme.onPrimary)
    }
}

private fun validatePassword(password: String): String? {
    return when {
        password.isBlank() -> "Password cannot be empty"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}