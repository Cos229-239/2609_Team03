package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.data.Vehicle
import java.util.Calendar

@Composable
fun VehicleProfileScreen(
    vehicle: Vehicle,
    onVehicleChange: (Vehicle) -> Unit,
    onBack: () -> Unit,
    repositoryError: String? = null
) {

    var make by remember(vehicle) {
        mutableStateOf(vehicle.make)
    }

    var model by remember(vehicle) {
        mutableStateOf(vehicle.model)
    }

    var year by remember(vehicle) {
        mutableStateOf(if (vehicle.year == 0) "" else vehicle.year.toString())
    }

    var mileage by remember(vehicle) {
        mutableStateOf(if (vehicle.id.isBlank() && vehicle.mileage == 0) "" else vehicle.mileage.toString())
    }

    var vin by remember(vehicle) {
        mutableStateOf(vehicle.vin)
    }
    var validationError by remember { mutableStateOf<String?>(null) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val minimumVehicleYear = 1886

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Vehicle Profile",
            fontSize = 32.sp
        )

        OutlinedTextField(
            value = make,
            onValueChange = { newMake -> make = newMake },
            label = { Text("Make") },
            singleLine = true
        )

        OutlinedTextField(
            value = model,
            onValueChange = { newModel -> model = newModel },
            label = { Text("Model") },
            singleLine = true
        )

        OutlinedTextField(
            value = year,
            onValueChange = { newYear -> year = newYear },
            label = { Text("Year") },
            singleLine = true
        )

        OutlinedTextField(
            value = mileage,
            onValueChange = { newMileage -> mileage = newMileage },
            label = { Text("Mileage") },
            singleLine = true
        )

        OutlinedTextField(
            value = vin,
            onValueChange = { newVin -> vin = newVin },
            label = { Text("VIN (optional)") },
            singleLine = true
        )

        (validationError ?: repositoryError)?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                val parsedYear = year.toIntOrNull()
                val parsedMileage = mileage.toIntOrNull()
                validationError = when {
                    make.isBlank() -> "Enter the vehicle make."
                    model.isBlank() -> "Enter the vehicle model."
                    parsedYear == null -> "Enter a valid whole-number year."
                    parsedYear !in minimumVehicleYear..(currentYear + 1) ->
                        "Enter a vehicle year from $minimumVehicleYear to ${currentYear + 1}."
                    parsedMileage == null -> "Enter a valid whole-number mileage."
                    parsedMileage < 0 -> "Mileage cannot be negative."
                    else -> null
                }

                if (validationError == null) {
                    onVehicleChange(
                        Vehicle(
                            id = vehicle.id,
                            year = parsedYear!!,
                            make = make.trim(),
                            model = model.trim(),
                            mileage = parsedMileage!!,
                            vin = vin.trim()
                        )
                    )
                }
            }
        ) {
            Text("Save Vehicle")
        }

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}