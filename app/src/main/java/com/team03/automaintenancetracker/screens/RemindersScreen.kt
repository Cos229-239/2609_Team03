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
fun ReminderScreen(onBack: () -> Unit){

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Reminders",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Upcoming Maintenance")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Oil Change Due")
        Text("500 Miles Remaining")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Tire Rotation Due")
        Text("1000 Miles Remaining")

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBack) {
            Text("Back")
        }
    }
}