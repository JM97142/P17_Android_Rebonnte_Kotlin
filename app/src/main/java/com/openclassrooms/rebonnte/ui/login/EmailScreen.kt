package com.openclassrooms.rebonnte.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailScreen(
    onLogInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.sign_in_email),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("sign_in") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onLogInClick,
                    modifier = Modifier
                        .padding(16.dp, bottom = 16.dp)
                        .width(300.dp)
                        .height(50.dp),
                    shape = RectangleShape,
                ) {
                    Text(
                        text = "Sign in with email address",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignUpClick,
                    modifier = Modifier
                        .padding(16.dp, bottom = 16.dp)
                        .width(300.dp)
                        .height(50.dp),
                    shape = RectangleShape,
                ) {
                    Text(
                        text = "Sign up with email address",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}