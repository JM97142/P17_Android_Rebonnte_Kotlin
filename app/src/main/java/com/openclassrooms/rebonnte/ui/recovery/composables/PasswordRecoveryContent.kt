package com.openclassrooms.rebonnte.ui.recovery.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun PasswordRecoveryContent(
    email: String,
    emailError: String?,
    isLoading: Boolean,
    showDialog: Boolean,
    onEmailChange: (String) -> Unit,
    onSendResetEmail: () -> Unit,
    onDismissDialog: () -> Unit,
    padding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.password_recovery_label),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(id = R.string.email), color = Color.Black) },
            isError = emailError != null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("RecoveryEmailField"),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                errorTextColor = Color.Red,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Gray,
                errorIndicatorColor = Color.Red
            )
        )

        emailError?.let {
            Text(text = it, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSendResetEmail,
            modifier = Modifier
                .padding(16.dp, bottom = 16.dp)
                .width(200.dp)
                .height(50.dp)
                .testTag("RecoverySendButton"),
            shape = RectangleShape,
            enabled = !isLoading
        ) {
            Text(
                if (isLoading) "Sending..." else "Send Reset Email",
                color = Color.Black
            )
        }

        if (showDialog) {
            PasswordRecoveryDialog(onDismissDialog, email)
        }
    }
}

@Composable
fun PasswordRecoveryDialog(onDismissDialog: () -> Unit, email: String) {
    AlertDialog(
        onDismissRequest = onDismissDialog,
        title = { Text("Password Reset", color = Color.Black) },
        text = {
            Text(
                text = stringResource(id = R.string.password_recovery_message, email),
                color = Color.Black
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismissDialog,
                modifier = Modifier.testTag("RecoveryDialogOkButton")
            ) {
                Text("OK", color = Color.Black)
            }
        }
    )
}