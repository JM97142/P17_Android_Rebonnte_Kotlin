package com.openclassrooms.rebonnte.ui.medicine.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openclassrooms.rebonnte.models.Aisle
import com.openclassrooms.rebonnte.models.Medicine

@Composable
fun MedicineItem(
    medicine: Medicine,
    aisle: Aisle,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = medicine.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray, fontSize = 14.sp)
            Text(text = "Aisle: ${aisle.name}", color = Color.Gray, fontSize = 14.sp)
        }
    }
}