package com.openclassrooms.rebonnte.ui.signup.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.R

@Composable
fun NameLastnameInput(
    name: String,
    lastname: String,
    onNameChange: (String) -> Unit,
    onLastnameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    var nameError by remember { mutableStateOf<String?>(null) }
    var lastnameError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.enter_name_surname),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.semantics { heading() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabeledTextField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(id = R.string.name),
            error = nameError,
            testTag = "NameField",
            contentDescription = "Champ nom"
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabeledTextField(
            value = lastname,
            onValueChange = onLastnameChange,
            label = stringResource(id = R.string.lastname),
            error = lastnameError,
            testTag = "LastNameField",
            contentDescription = "Champ prénom"
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                nameError = if (name.isBlank()) "Name cannot be empty" else null
                lastnameError = if (lastname.isBlank()) "Surname cannot be empty" else null
                if (nameError == null && lastnameError == null) onNext()
            },
            modifier = Modifier
                .padding(16.dp, bottom = 16.dp)
                .width(150.dp)
                .height(50.dp)
                .testTag("NameNextButton")
                .semantics { contentDescription = "Bouton suivant" },
            shape = RectangleShape
        ) {
            Text(
                text = stringResource(id = R.string.next),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabeledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String? = null,
    testTag: String,
    contentDescription: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = error != null,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
                .semantics { this.contentDescription = contentDescription },
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
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}