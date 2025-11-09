package com.openclassrooms.rebonnte.di

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.repository.AisleRepository
import com.openclassrooms.rebonnte.repository.MedicineRepository
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.signin.SignInViewModel
import com.openclassrooms.rebonnte.ui.signup.SignUpViewModel
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { FirebaseFirestore.getInstance() }
    single { com.google.firebase.auth.FirebaseAuth.getInstance() }

    single<SharedPreferences> {
        get<Context>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
    single {
        GoogleAuthClient(context = androidContext(), oneTapClient = Identity.getSignInClient(androidContext()))
    }
    single { EmailAuthClient() }

    viewModel { LoginViewModel(get()) }
    viewModel { AisleViewModel(get()) }
    viewModel { MedicineViewModel(get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { SignUpViewModel(get(), get()) }

    single { AisleRepository(get()) }
    single { MedicineRepository(get()) }
}