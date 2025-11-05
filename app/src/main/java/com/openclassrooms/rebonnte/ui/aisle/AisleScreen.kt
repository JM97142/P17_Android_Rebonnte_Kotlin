package com.openclassrooms.rebonnte.ui.aisle

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.openclassrooms.rebonnte.ui.aisle.composables.AisleItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun AisleScreen(
    navController: NavController,
    aisleViewModel: AisleViewModel = koinViewModel()
) {
    val aisles by aisleViewModel.aisles.collectAsState(initial = emptyList())
    LaunchedEffect(aisles) {
        println("Aisles changed, forcing recomposition: $aisles")
    }

    BackHandler(enabled = true) {
        navController.navigate("medicine") {
            popUpTo(navController.graph.startDestinationId) { inclusive = false }
            launchSingleTop = true
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(aisles, key = { it.id }) { aisle ->
            SwipeToDeleteItem(
                onDelete = { aisleViewModel.deleteAisle(aisle) }
            ) {
                AisleItem(
                    aisle = aisle,
                    onClick = { navController.navigate("aisle_detail/${aisle.name}") }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDeleteItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState()
    var showDialog by remember { mutableStateOf(false) }
    var resetSwipe by remember { mutableStateOf(false) }

    // Observe dismiss state and show the dialog when swiped
    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.isDismissed(DismissDirection.StartToEnd) ||
            dismissState.isDismissed(DismissDirection.EndToStart)
        ) {
            showDialog = true
        }
    }

    // Reset the swipe
    LaunchedEffect(resetSwipe) {
        if (resetSwipe) {
            dismissState.reset()
            resetSwipe = false
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                resetSwipe = true
            },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    resetSwipe = true
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        background = {
            val color = if (dismissState.targetValue == DismissValue.DismissedToStart ||
                dismissState.targetValue == DismissValue.DismissedToEnd
            ) Color.Red else MaterialTheme.colorScheme.surface

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        },
        dismissContent = { content() }
    )
}