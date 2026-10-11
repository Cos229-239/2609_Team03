package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VehicleProfileScreen(onBack : () -> Unit){
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Vehicle Profile",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Make: Honda")
        Text("Model: Pilot")
        Text("Year: 2020")
        Text("Mileage: 50,000")

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBack){
            Text("Back")
        }
    }
}