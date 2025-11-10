package com.openclassrooms.rebonnte.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.login.LoginViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.nav.AppNavGraph
import com.openclassrooms.rebonnte.ui.nav.BottomNavBar
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UiApp(
    googleAuthUiClient: GoogleAuthClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope,
    loginViewModel: LoginViewModel,
    aisleViewModel: AisleViewModel,
    medicineViewModel: MedicineViewModel
) {
    val navController = rememberNavController()

    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            if (currentRoute in listOf("aisle", "medicine")) {
                Column {
                    TopAppBar(
                        title = { Text(if (currentRoute == "aisle") "Aisles" else "Medicines") },
                        actions = {
                            if (currentRoute == "medicine") {
                                DropDownMenu(medicineViewModel)
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    // On affiche la SearchBar uniquement pour "medicine"
                    if (currentRoute == "medicine") {
                        EmbeddedSearchBar(
                            query = searchQuery,
                            onQueryChange = { newQuery ->
                                searchQuery = newQuery
                                medicineViewModel.filterByName(newQuery)
                            },
                            isSearchActive = isSearchActive,
                            onActiveChanged = { isSearchActive = it },
                            modifier = Modifier
                                .padding(bottom = 4.dp)
                                .testTag("Search")
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (currentRoute in listOf("aisle", "medicine")) {
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    googleAuthUiClient = googleAuthUiClient,
                    emailAuthClient = emailAuthClient,
                    aisleViewModel = aisleViewModel,
                    onSignOut = {}
                )
            }
        },
        floatingActionButton = {
            if (currentRoute in listOf("aisle", "medicine")) {
                FloatingActionButton(onClick = {
                    when (currentRoute) {
                        "medicine" -> navController.navigate("add_medicine")
                        "aisle" -> aisleViewModel.addRandomAisle()
                    }
                }) { Icon(Icons.Default.Add, contentDescription = "Add") }
            }
        }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            aisleViewModel = aisleViewModel,
            medicineViewModel = medicineViewModel,
            googleAuthClient = googleAuthUiClient,
            emailAuthClient = emailAuthClient,
            modifier = Modifier.padding(paddingValues)
        )
    }
}