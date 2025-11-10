package com.openclassrooms.rebonnte.ui.medicine

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.models.Medicine
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewMedicineScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel,
    aisleViewModel: AisleViewModel,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    val aisles by aisleViewModel.aisles.collectAsState(initial = emptyList())
    var name by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var selectedAisle by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Medicine",
                        modifier = Modifier
                            .testTag("topBarTitle")
                            .semantics {
                                heading()
                                contentDescription = "Ajouter un nouveau médicament"
                            }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.semantics { contentDescription = "Retour" }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicine Name
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Medicine Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("addNameMedicineField")
                    .semantics { contentDescription = "Champ pour le nom du médicament" }
            )

            // Stock
            TextField(
                value = stock,
                onValueChange = { if (it.all { char -> char.isDigit() }) stock = it },
                label = { Text("Stock") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("addStockMedicineField")
                    .semantics { contentDescription = "Champ pour le stock du médicament" }
            )

            // Aisle Picker
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("aisleDropdown")
                    .semantics { contentDescription = "Sélecteur d'allée" }
            ) {
                TextField(
                    value = selectedAisle,
                    onValueChange = { },
                    label = { Text("Aisle Name") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .testTag("aisleField")
                        .semantics { contentDescription = "Champ de sélection d'allée" }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (aisles.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No aisles available") },
                            onClick = { },
                            modifier = Modifier.semantics { contentDescription = "Aucune allée disponible" }
                        )
                    } else {
                        aisles.forEach { aisle ->
                            DropdownMenuItem(
                                text = { Text(aisle.name) },
                                onClick = {
                                    selectedAisle = aisle.name
                                    expanded = false
                                },
                                modifier = Modifier
                                    .testTag("aisleItem_${aisle.name}")
                                    .semantics { contentDescription = "Allée ${aisle.name}" }
                            )
                        }
                    }
                }
            }

            // Add Button
            Button(
                onClick = {
                    if (name.isBlank() || stock.isBlank() || selectedAisle.isBlank()) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields")
                        }
                    } else {
                        val newMedicine = Medicine(
                            name = name,
                            stock = stock.toInt(),
                            nameAisle = selectedAisle
                        )
                        medicineViewModel.addNewMedicine(newMedicine)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Medicine added")
                        }
                        navController.popBackStack()
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("addMedicineButton")
                    .semantics { contentDescription = "Bouton pour ajouter le médicament" }
            ) {
                Text("Add Medicine")
            }
        }
    }
}