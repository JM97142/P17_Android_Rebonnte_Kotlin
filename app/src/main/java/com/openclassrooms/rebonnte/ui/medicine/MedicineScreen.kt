package com.openclassrooms.rebonnte.ui.medicine

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel
import com.openclassrooms.rebonnte.ui.medicine.composables.MedicineItem
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun MedicineScreen(
    navController: NavController,
    medicineViewModel: MedicineViewModel = koinViewModel()
) {
    val medicines by medicineViewModel.medicines.collectAsState(initial = emptyList())

    LaunchedEffect(medicines) {
        println("Medicines changed, forcing recomposition: $medicines")
    }

    BackHandler(enabled = true) {
        navController.navigate("aisle") {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(medicines, key = { it.id ?: UUID.randomUUID().toString() }) { medicine ->
            SwipeToDeleteItem(
                onDelete = { medicineViewModel.deleteMedicine(medicine.id) }
            ) {
                val aisle = medicineViewModel.getAisleForMedicine(medicine.id)

                MedicineItem(
                    medicine = medicine,
                    aisle = aisle,
                    onClick = {
                        navController.navigate("medicine_detail/${medicine.name}")
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState()
    var showDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(swipeState.currentValue) {
        if (swipeState.currentValue == SwipeToDismissBoxValue.EndToStart ||
            swipeState.currentValue == SwipeToDismissBoxValue.StartToEnd
        ) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                coroutineScope.launch {
                    swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                }
            },
            title = {
                Text(
                    "Confirm Deletion",
                    modifier = Modifier.semantics { heading() }
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this item?",
                    modifier = Modifier.semantics {
                        liveRegion = LiveRegionMode.Assertive
                        contentDescription = "Confirmation de suppression"
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                        coroutineScope.launch {
                            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = "Confirmer la suppression" }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            swipeState.snapTo(SwipeToDismissBoxValue.Settled)
                        }
                    },
                    modifier = Modifier.semantics { contentDescription = "Annuler la suppression" }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    SwipeToDismissBox(
        state = swipeState,
        enableDismissFromStartToEnd = true,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            val color = when (swipeState.targetValue) {
                SwipeToDismissBoxValue.EndToStart,
                SwipeToDismissBoxValue.StartToEnd -> Color.Red
                else -> MaterialTheme.colorScheme.surface
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer le médicament",
                    tint = Color.White
                )
            }
        },
        content = { content() }
    )
}