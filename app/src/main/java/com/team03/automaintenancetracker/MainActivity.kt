package com.team03.automaintenancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.team03.automaintenancetracker.data.Vehicle
import com.team03.automaintenancetracker.data.VehicleRepository
import com.team03.automaintenancetracker.screens.HomeScreen
import com.team03.automaintenancetracker.screens.MaintenanceHistoryScreen
import com.team03.automaintenancetracker.screens.OilChangeGuideScreen
import com.team03.automaintenancetracker.screens.ReminderScreen
import com.team03.automaintenancetracker.screens.VehicleProfileScreen
import com.team03.automaintenancetracker.ui.theme.AutoMaintenanceTrackerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoMaintenanceTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VehicleScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun VehicleScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var currentScreen by remember {
        mutableStateOf("home")
    }

    var vehicle by remember {
        mutableStateOf(
            Vehicle(
                id = "",
                make = "",
                model = "",
                year = 0,
                mileage = 0,
                vin = ""
            )
        )
    }
    var repositoryError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(context) {
        try {
            val savedVehicles = withContext(Dispatchers.IO) {
                VehicleRepository.loadVehicles(context)
            }
            if (vehicle.id.isBlank()) {
                vehicle = savedVehicles.firstOrNull() ?: vehicle
            }
        } catch (error: IOException) {
            repositoryError = "Could not load saved vehicles: ${error.message ?: "Storage error"}"
        } catch (error: JSONException) {
            repositoryError = "Could not read saved vehicles: ${error.message ?: "Invalid vehicle data"}"
        } catch (error: IllegalArgumentException) {
            repositoryError = "Could not load saved vehicles: ${error.message ?: "Invalid vehicle data"}"
        }
    }

    when (currentScreen) {
        "home" -> {
            HomeScreen(
                modifier = modifier,
                onVehicleClick = {
                    currentScreen = "vehicle"},

                onHistoryClick = {
                    currentScreen = "history"},

                onOilClick = {
                    currentScreen = "oil"},

                onReminderClick = {
                    currentScreen = "reminders"}
            )
        }

        "vehicle" -> {
            VehicleProfileScreen(
                vehicle = vehicle,
                repositoryError = repositoryError,
                onVehicleChange = { updatedVehicle ->
                    try {
                        vehicle = VehicleRepository.saveVehicle(context, updatedVehicle)
                        repositoryError = null
                    } catch (error: IOException) {
                        repositoryError = "Could not save vehicle: ${error.message ?: "Storage error"}"
                    } catch (error: JSONException) {
                        repositoryError = "Could not save vehicle: ${error.message ?: "Invalid vehicle data"}"
                    } catch (error: IllegalArgumentException) {
                        repositoryError = "Could not save vehicle: ${error.message ?: "Invalid vehicle data"}"
                    }
                },
                onBack = { currentScreen = "home" }
            )
        }

        "history" -> {
            MaintenanceHistoryScreen(
                vehicle = vehicle,
                onBack = { currentScreen = "home" })
        }

        "oil" -> {
            OilChangeGuideScreen(
                onBack = { currentScreen = "home" })
        }

        "reminders" -> {
            ReminderScreen(
                onBack = { currentScreen = "home" })
        }
    }
}


@Preview(showBackground = true)
@Composable
fun VehicleScreenPreview() {
    AutoMaintenanceTrackerTheme {
        VehicleScreen()
    }
}