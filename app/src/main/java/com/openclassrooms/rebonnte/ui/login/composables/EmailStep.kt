package com.openclassrooms.rebonnte.ui.login.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailStep(
    email: MutableState<String>,
    emailError: MutableState<String?>,
    onNext: () -> Unit
) {
    TextField(
        value = email.value,
        onValueChange = { email.value = it },
        label = { Text("Email Address") },
        isError = emailError.value != null,
        modifier = Modifier.fillMaxWidth()
    )
    emailError.value?.let {
        Text(text = it, color = Color.Red, fontSize = 12.sp)
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp),
        shape = RectangleShape,
    ) {
        Text("Next", color = Color.White)
    }
}