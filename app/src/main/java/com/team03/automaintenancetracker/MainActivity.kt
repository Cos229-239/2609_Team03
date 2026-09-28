package com.team03.automaintenancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.team03.automaintenancetracker.ui.theme.AutoMaintenanceTrackerTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoMaintenanceTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VehicleScreen( modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text("Auto Maintenance Tracker")

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth())
        {
            Text("Vehicle Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth())
        {
            Text("Maintenance History")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth())
        {
            Text("Oil Change Guide")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth())
        {
            Text("Reminders")
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