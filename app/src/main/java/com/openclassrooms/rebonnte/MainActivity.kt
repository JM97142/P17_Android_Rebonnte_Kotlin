package com.openclassrooms.rebonnte

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.openclassrooms.rebonnte.ui.aisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.AddNewMedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.nav.BottomNavBar
import com.openclassrooms.rebonnte.ui.nav.appNavigation
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import com.openclassrooms.rebonnte.utils.EmailAuthClient
import com.openclassrooms.rebonnte.utils.GoogleAuthClient
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private val emailAuthClient by lazy { EmailAuthClient() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(
                googleAuthUiClient = googleAuthUiClient,
                emailAuthClient = emailAuthClient,
                lifecycleScope = lifecycleScope
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(
    googleAuthUiClient: GoogleAuthClient,
    emailAuthClient: EmailAuthClient,
    lifecycleScope: LifecycleCoroutineScope
) {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = koinViewModel()
    val aisleViewModel: AisleViewModel = koinViewModel()

    // States de recherche stables
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    RebonnteTheme {
        Scaffold(
            topBar = {
                if (currentRoute in listOf("aisle", "medicine")) {
                    TopAppBar(
                        title = { Text(if (currentRoute == "aisle") "Aisles" else "Medicines") },
                        actions = {
                            if (currentRoute == "medicine") {
                                var expanded by remember { mutableStateOf(false) }
                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "Sort options")
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("Sort by None") },
                                            onClick = {
                                                medicineViewModel.sortByNone()
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Sort by Name") },
                                            onClick = {
                                                medicineViewModel.sortByName()
                                                expanded = false
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("Sort by Stock") },
                                            onClick = {
                                                medicineViewModel.sortByStock()
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )

                    if (currentRoute == "medicine") {
                        EmbeddedSearchBar(
                            query = searchQuery,
                            onQueryChange = { newQuery ->
                                searchQuery = newQuery
                                medicineViewModel.filterByName(newQuery)
                            },
                            isSearchActive = isSearchActive,
                            onActiveChanged = { isSearchActive = it }
                        )
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
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "sign_in",
                modifier = Modifier.padding(paddingValues)
            ) {
                appNavigation(navController, googleAuthUiClient, emailAuthClient, lifecycleScope)
                composable("aisle") { AisleScreen(navController, aisleViewModel) }
                composable("medicine") { MedicineScreen(navController, medicineViewModel) }

                composable("aisle_detail/{aisleName}") { backStackEntry ->
                    val aisleName = backStackEntry.arguments?.getString("aisleName")?.let { Uri.decode(it) } ?: "Unknown"
                    AisleDetailScreen(aisleName, medicineViewModel, navController)
                }

                composable("medicine_detail/{medicineName}") { backStackEntry ->
                    val medicineName = backStackEntry.arguments?.getString("medicineName")?.let { Uri.decode(it) } ?: "Unknown"
                    MedicineDetailScreen(medicineName, medicineViewModel) {
                        navController.popBackStack()
                    }
                }

                composable("add_medicine") {
                    AddNewMedicineScreen(navController, medicineViewModel, aisleViewModel)
                }
            }
        }
    }
}

@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        placeholder = { Text("Search") },
        leadingIcon = {
            Icon(
                imageVector = if (isSearchActive) Icons.AutoMirrored.Rounded.ArrowBack else Icons.Rounded.Search,
                contentDescription = if (isSearchActive) "Close search" else "Search icon",
                modifier = Modifier.clickable(
                    indication = null,  // pas d'effet de ripple pour éviter le conflit
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onActiveChanged(!isSearchActive)
                }
            )
        },
        trailingIcon = {
            if (isSearchActive && query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp)
    )
}