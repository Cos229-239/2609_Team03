package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.models.Vehicle

@Composable
fun VehicleProfileScreen(onBack : () -> Unit){

    val vehicle = Vehicle(
        make = "Honda",
        model = "Pilot",
        year = 2020,
        mileage = 50000,
        vin = "123456789"
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Vehicle Profile",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Make: ${vehicle.make}")
        Text("Model: ${vehicle.model}")
        Text("Year: ${vehicle.year}")
        Text("Mileage: ${vehicle.mileage}")
        Text("VIN: ${vehicle.vin}")

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBack){
            Text("Back")
        }
    }
}