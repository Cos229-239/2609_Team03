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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*

@Composable
fun VehicleProfileScreen(
    vehicle: Vehicle,
    onVehicleChange: (Vehicle) -> Unit,
    onBack : () -> Unit){

    var make by remember {
        mutableStateOf(vehicle.make)
    }

    var model by remember {
        mutableStateOf(vehicle.model)
    }

    var year by remember {
        mutableStateOf(if(vehicle.year ==0) "" else vehicle.year.toString())
    }

    var mileage by remember {
        mutableStateOf(if(vehicle.mileage == 0) "" else vehicle.mileage.toString())
    }

    var vin by remember {
        mutableStateOf(vehicle.vin)
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Vehicle Profile",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = make,
            onValueChange = { newMake -> make = newMake},
            label = {Text("Make")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { newModel -> model = newModel},
            label = {Text("Model")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = year,
            onValueChange = { newYear -> year = newYear},
            label = {Text("Year")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = mileage,
            onValueChange = { newMileage -> mileage = newMileage},
            label = {Text("Mileage")}
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = vin,
            onValueChange = { newVin -> vin = newVin},
            label = {Text("VIN")}
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            val updatedVehicle = Vehicle(
                make = make,
                model = model,
                year = year.toIntOrNull() ?: 0,
                mileage = mileage.toIntOrNull() ?: 0,
                vin = vin
            )
            onVehicleChange(updatedVehicle)
        }){
            Text("Save Vehicle")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onBack){
            Text("Back")
        }
    }
}