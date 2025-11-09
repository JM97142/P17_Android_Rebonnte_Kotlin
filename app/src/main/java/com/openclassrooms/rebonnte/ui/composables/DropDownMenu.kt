package com.openclassrooms.rebonnte.ui.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel

@Composable
fun DropDownMenu(
    medicineViewModel: MedicineViewModel
) {
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