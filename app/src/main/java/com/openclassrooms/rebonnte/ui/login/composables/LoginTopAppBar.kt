package com.openclassrooms.rebonnte.ui.login.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTopAppBar(currentStep: MutableState<Int>, navController: NavController) {
    TopAppBar(title = {
        Text(
            text = stringResource(id = R.string.sign_in),
            color = MaterialTheme.colorScheme.onSurface
        )
    },
        navigationIcon = {
            IconButton(
                onClick = {
                    if (currentStep.value > 1) {
                        currentStep.value -= 1
                    } else {
                        navController.navigate("email_sign_in")
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}