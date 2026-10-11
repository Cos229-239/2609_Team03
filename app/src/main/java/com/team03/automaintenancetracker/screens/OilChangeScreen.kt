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
fun OilChangeGuideScreen(onBack: () -> Unit){

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(120.dp))

        Text(
            text = "Oil Change Guide",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Required Tools")
        Text("-Oil Filter Wrench")
        Text("-Socket Set")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Required PPE")
        Text("-Gloves")
        Text("-Safety Glasses")

        Spacer(modifier = Modifier.height(20.dp))

        Text("Step 1: Drain Oil")
        Text("Step 2: Replace Filter")
        Text("Step 3: Add Oil")

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBack){
            Text("Back")
        }
    }
}

