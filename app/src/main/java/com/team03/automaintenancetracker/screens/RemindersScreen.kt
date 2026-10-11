package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.data.MaintenanceRecommendationService
import com.team03.automaintenancetracker.data.ScheduledMaintenance
import com.team03.automaintenancetracker.data.Vehicle
import com.team03.automaintenancetracker.data.VehicleMaintenance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RemindersScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var vehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var selectedVehicleId by remember { mutableStateOf<String?>(null) }
    var recommendations by remember { mutableStateOf<VehicleMaintenance?>(null) }
    var isLoadingVehicles by remember { mutableStateOf(true) }
    var isLoadingRecommendations by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load saved vehicles from the same JSON data used by the native service.
    LaunchedEffect(context) {
        try {
            val loadedVehicles = withContext(Dispatchers.IO) {
                MaintenanceRecommendationService.loadVehicles(context)
            }
            vehicles = loadedVehicles
            selectedVehicleId = loadedVehicles.firstOrNull()?.id
        } catch (error: Exception) {
            errorMessage = "Could not load vehicles: ${error.message ?: "Unknown error"}"
        } finally {
            isLoadingVehicles = false
        }
    }

    // A selected vehicle ID is passed to the recommendation service.
    LaunchedEffect(selectedVehicleId) {
        val vehicleId = selectedVehicleId ?: return@LaunchedEffect
        isLoadingRecommendations = true
        errorMessage = null
        try {
            recommendations = withContext(Dispatchers.IO) {
                MaintenanceRecommendationService.getVehicleMaintenance(context, vehicleId)
            }
            if (recommendations == null) {
                errorMessage = "Could not find the selected vehicle."
            }
        } catch (error: Exception) {
            errorMessage = "Could not load maintenance: ${error.message ?: "Unknown error"}"
        } finally {
            isLoadingRecommendations = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Reminders", fontSize = 32.sp)
        Button(onClick = onBack) {
            Text("Back")
        }

        when {
            isLoadingVehicles -> Text("Loading vehicles...")
            errorMessage != null -> Text(errorMessage.orEmpty())
            vehicles.isEmpty() -> Text("No vehicle selected. Add a vehicle to see maintenance recommendations.")
            else -> {
                VehicleSelector(
                    vehicles = vehicles,
                    selectedVehicleId = selectedVehicleId,
                    onVehicleSelected = { selectedVehicleId = it }
                )

                when {
                    isLoadingRecommendations -> Text("Loading maintenance recommendations...")
                    recommendations != null -> {
                        val result = recommendations!!
                        Text(
                            "${result.vehicle.year} ${result.vehicle.make} " +
                                "${result.vehicle.model} | ${result.vehicle.mileage} mi"
                        )
                        MaintenanceSection(
                            title = "Due",
                            emptyMessage = "No maintenance currently due.",
                            items = result.due,
                            currentMileage = result.vehicle.mileage
                        )
                        MaintenanceSection(
                            title = "Upcoming",
                            emptyMessage = "No upcoming maintenance.",
                            items = result.upcoming,
                            currentMileage = result.vehicle.mileage
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VehicleSelector(
    vehicles: List<Vehicle>,
    selectedVehicleId: String?,
    onVehicleSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedVehicle = vehicles.find { it.id == selectedVehicleId }

    Column {
        Button(onClick = { expanded = true }) {
            Text(selectedVehicle?.let { "${it.year} ${it.make} ${it.model}" } ?: "Select vehicle")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            vehicles.forEach { vehicle ->
                DropdownMenuItem(
                    text = { Text("${vehicle.year} ${vehicle.make} ${vehicle.model}") },
                    onClick = {
                        onVehicleSelected(vehicle.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun MaintenanceSection(
    title: String,
    emptyMessage: String,
    items: List<ScheduledMaintenance>,
    currentMileage: Int
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, fontSize = 20.sp)
        if (items.isEmpty()) {
            Text(emptyMessage)
        } else {
            items.forEach { scheduled ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(scheduled.item.name, fontSize = 18.sp)
                        scheduled.item.category?.let { Text(it) }
                        val mileageStatus = if (title == "Due") {
                            "Due at ${scheduled.dueMileage} ${scheduled.item.mileageUnit}"
                        } else {
                            "Due in ${scheduled.dueMileage - currentMileage} " +
                                "${scheduled.item.mileageUnit} (at ${scheduled.dueMileage})"
                        }
                        Text(mileageStatus)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}
