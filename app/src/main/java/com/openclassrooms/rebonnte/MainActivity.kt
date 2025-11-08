package com.openclassrooms.rebonnte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.identity.Identity
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.composables.UiApp
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val emailAuthClient by lazy { EmailAuthClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loginViewModel: LoginViewModel = get()
        val aisleViewModel: AisleViewModel = get()
        val medicineViewModel: MedicineViewModel = get()

        setContent {
            RebonnteTheme {
                UiApp(
                    googleAuthUiClient = googleAuthClient,
                    emailAuthClient = emailAuthClient,
                    lifecycleScope = lifecycleScope,
                    loginViewModel = loginViewModel,
                    aisleViewModel = aisleViewModel,
                    medicineViewModel = medicineViewModel
                )
            }
        }
    }
}