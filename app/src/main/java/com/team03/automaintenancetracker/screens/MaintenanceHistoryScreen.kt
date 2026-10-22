package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.models.MaintenanceRecord
import com.team03.automaintenancetracker.data.Vehicle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
@Composable
fun MaintenanceHistoryScreen(
    vehicle: Vehicle,
    onBack: () -> Unit){

    var serviceType by remember { mutableStateOf("")}

    var serviceMileage by remember { mutableStateOf("")}

    var serviceDate by remember { mutableStateOf("")}

    var serviceNotes by remember { mutableStateOf("")}

    var maintenanceRecord by remember { mutableStateOf(listOf<MaintenanceRecord>())}
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Maintenance History",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${vehicle.year} ${vehicle.make} ${vehicle.model}",
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Mileage: ${vehicle.mileage}")

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = serviceType,
            onValueChange = { newServiceType -> serviceType = newServiceType },
            label = { Text("Service Type") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serviceMileage,
            onValueChange = { newMileage -> serviceMileage = newMileage },
            label = { Text("Mileage") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serviceDate,
            onValueChange = { newDate -> serviceDate = newDate },
            label = { Text("Date") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = serviceNotes,
            onValueChange = { newNotes -> serviceNotes = newNotes },
            label = { Text("Notes") }
        )

        validationError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            val parsedMileage = serviceMileage.toIntOrNull()
            validationError = when {
                serviceType.isBlank() -> "Enter a service type."
                serviceMileage.isBlank() -> "Enter a mileage."
                parsedMileage == null -> "Enter a valid whole-number mileage."
                parsedMileage < 0 -> "Mileage cannot be negative."
                else -> null
            }

            if (validationError != null || parsedMileage == null) {
                return@Button
            }

            val newRecord = MaintenanceRecord(
                maintenanceType = serviceType,
                mileage = parsedMileage,
                completedDate = serviceDate,
                notes = serviceNotes
            )
            maintenanceRecord = maintenanceRecord + newRecord

            serviceType = ""
            serviceMileage = ""
            serviceDate = ""
            serviceNotes = ""
        }) {
            Text("Save Record")
        }

        if (maintenanceRecord.isNotEmpty()) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Saved Maintenance Record", fontSize = 22.sp)

            Spacer(modifier = Modifier.height(8.dp))

            maintenanceRecord.forEach { record ->

                Text("Service: ${record.maintenanceType}")
                Text("Mileage: ${record.mileage}")
                Text("Date: ${record.completedDate}")
                Text("Notes: ${record.notes}")
            }
        }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBack
            ) {
                Text("Back")
            }
        }
    }
