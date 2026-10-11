package com.team03.automaintenancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team03.automaintenancetracker.screens.HomeScreen
import com.team03.automaintenancetracker.screens.MaintenanceHistoryScreen
import com.team03.automaintenancetracker.screens.OilChangeGuideScreen
import com.team03.automaintenancetracker.screens.ReminderScreen
import com.team03.automaintenancetracker.screens.VehicleProfileScreen
import com.team03.automaintenancetracker.ui.theme.AutoMaintenanceTrackerTheme

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
    var currentScreen by remember {
        mutableStateOf("home")
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
                onBack = { currentScreen = "home" })
        }

        "history" -> {
            MaintenanceHistoryScreen(
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