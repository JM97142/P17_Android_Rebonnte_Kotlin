package com.openclassrooms.rebonnte.ui.signup.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTopBar(currentStep: Int, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.sign_up), color = Color.Black) },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.testTag("SignUpBackButton")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = Color.Black
                )
            }
        }
    )
}