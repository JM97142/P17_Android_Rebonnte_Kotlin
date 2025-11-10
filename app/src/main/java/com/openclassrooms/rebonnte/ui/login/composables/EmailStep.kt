package com.openclassrooms.rebonnte.ui.login.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmailStep(
    email: String,
    emailError: String?,
    onEmailChange: (String) -> Unit,
    onNext: () -> Unit
) {
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Email Address") },
        isError = emailError != null,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("emailTextField")
            .semantics {
                contentDescription = "Champ pour saisir l’adresse e-mail"
            }
    )

    emailError?.let {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier
                .testTag("emailErrorText")
                .semantics {
                    liveRegion = LiveRegionMode.Assertive
                    contentDescription = "Erreur : $it"
                }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = onNext,
        modifier = Modifier
            .padding(16.dp, bottom = 16.dp)
            .width(150.dp)
            .height(50.dp)
            .testTag("nextButton")
            .semantics { contentDescription = "Bouton pour passer à l’étape suivante" },
        shape = RectangleShape,
    ) {
        Text(
            "Next",
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}