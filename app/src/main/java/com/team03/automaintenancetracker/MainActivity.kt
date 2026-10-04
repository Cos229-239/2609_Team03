package com.team03.automaintenancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.screens.HomeScreen
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
            ScreenPage(
                title = "Vehicle Profile",
                onBack = { currentScreen = "home" })
        }

        "history" -> {
            ScreenPage(
                title = "Maintenance History",
                onBack = { currentScreen = "home" })
        }

        "oil" -> {
            ScreenPage(
                title = "Oil Change Guide",
                onBack = { currentScreen = "home" })
        }

        "reminders" -> {
            ScreenPage(
                title = "Reminders",
                onBack = { currentScreen = "home" })
        }
    }
}

@Composable
fun ScreenPage(
    title: String, onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = title,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBack
        )
        {
            Text("Back")
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