package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.team03.automaintenancetracker.models.Reminders

@Composable
fun ReminderScreen(onBack: () -> Unit){

    val reminders = Reminders(
        title = "Oil Change Due",
        milesRemaining = 500
    )

    val tireReminder = Reminders(
        title = "Tire Rotation Due",
        milesRemaining = 1000
    )

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Reminders",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(reminders.title)

        Spacer(modifier = Modifier.height(20.dp))

        Text("${reminders.milesRemaining} Miles Remaining")

        Spacer(modifier = Modifier.height(20.dp))

        Text(tireReminder.title)

        Spacer(modifier = Modifier.height(20.dp))

        Text("${tireReminder.milesRemaining} Miles Remaining")

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onBack) {
            Text("Back")
        }
    }
}