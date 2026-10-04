package com.team03.automaintenancetracker.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.*

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onVehicleClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onOilClick: () -> Unit,
    onReminderClick: () -> Unit)
{
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(
            text = "PitStop", fontSize = 40.sp
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            onClick = onVehicleClick)
        {
            Text("Vehicle Profile")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            onClick = onHistoryClick)
        {
            Text("Maintenance History")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            onClick = onOilClick)
        {
            Text("Oil Change Guide")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(70.dp),
            onClick = onReminderClick)
        {
            Text("Reminders")
        }
    }
}