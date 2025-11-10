package com.openclassrooms.rebonnte.ui.login.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PasswordStep(
    email: String,
    password: String,
    passwordError: String?,
    passwordVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        keyboardOptions = KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        isError = passwordError != null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("passwordTextField")
            .semantics { contentDescription = "Champ pour saisir le mot de passe" },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Masquer le mot de passe" else "Afficher le mot de passe"
            IconButton(
                onClick = onPasswordVisibilityToggle,
                modifier = Modifier
                    .testTag("togglePasswordVisibility")
                    .semantics { contentDescription = description }
            ) {
                Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.onBackground)
            }
        }
    )

    passwordError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier
                .testTag("passwordErrorText")
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Erreur : $it"
                }
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSignIn,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp)
            .testTag("signInButton")
            .semantics { contentDescription = "Bouton pour se connecter" },
        shape = RectangleShape,
    ) {
        Text(text = "Sign In", color = MaterialTheme.colorScheme.onPrimary)
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(
        onClick = onForgotPassword,
        modifier = Modifier
            .testTag("forgotPasswordButton")
            .semantics { contentDescription = "Bouton mot de passe oublié" }
    ) {
        Text("Trouble signing in?", color = MaterialTheme.colorScheme.onBackground)
    }
}