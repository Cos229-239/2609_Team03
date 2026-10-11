package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.models.MaintenanceRecord

@Composable
fun MaintenanceHistoryScreen(onBack: () -> Unit){

    val oilChange = MaintenanceRecord(
        maintenanceType = "Oil Change",
        mileage = 50000,
        completedDate = "09/20/2026",
        notes = "Full Synthetic Oil"
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Maintenance History",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Service: ${oilChange.maintenanceType}")
        Text("Mileage: ${oilChange.mileage}")
        Text("Date: ${oilChange.completedDate}")
        Text("Notes: ${oilChange.notes}")

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBack){
            Text("Back")
        }
    }
}