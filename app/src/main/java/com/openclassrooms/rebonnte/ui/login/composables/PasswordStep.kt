package com.openclassrooms.rebonnte.ui.login.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun PasswordStep(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordError: MutableState<String?>,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    onSignIn: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))

    TextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text("Password") },
        isError = passwordError.value != null,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"
            IconButton(onClick = onPasswordVisibilityToggle) {
                Icon(imageVector = image, contentDescription = description, tint = Color.Black)
            }
        }
    )
    passwordError.value?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }

    Spacer(modifier = Modifier.height(32.dp))

    Button(
        onClick = onSignIn,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp),
        shape = RectangleShape,
    ) {
        Text(text = stringResource(id = R.string.sign_in), color = Color.Black)
    }

    Spacer(modifier = Modifier.height(16.dp))

    TextButton(onClick = onForgotPassword) {
        Text("Trouble signing in?", color = Color.Black)
    }
}