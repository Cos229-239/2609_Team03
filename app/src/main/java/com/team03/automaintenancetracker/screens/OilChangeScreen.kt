package com.team03.automaintenancetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun OilChangeGuideScreen(onBack: () -> Unit){

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally){

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Oil Change Guide",
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text("Required Tools", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text("-Oil Filter Wrench")
        Text("-Socket Set")
        Text("-Ratchet")
        Text("-Oil Drain Pan")
        Text("-Funnel")
        Text("-Jack and Jack Stands (if required)")

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Required Materials", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text("-New Engine Oil")
        Text("-New Oil Filter")
        Text("-Shop Towels")
        Text("-New Drain Plug Washer (if applicable)")

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Required PPE", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text("-Mechanic Gloves")
        Text("-Safety Glasses")
        Text("-Closed-Toed Shoes")

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Oil Change Procedure", fontSize = 22.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text("Step 1: Park the vehicle on a flat surface and engage the parking brake.")
        Text("Step 2: Allow the engine to cool if it has been running recently.")
        Text("Step 3: Raise the vehicle with a jack and support it using jack stands if necessary.")
        Text("Step 4: Place the drain pan underneath the oil pan drain plug.")
        Text("Step 5: Remove the drain plug and allow all engine oil to drain into the pan.")
        Text("Step 6: Remove old filter using an oil filter wrench.")
        Text("Step 7: Lightly coat the gasket of the new oil filter with fresh oil and install it.")
        Text("Step 8: Reinstall abd tighten the drain plug according to manufacturer specifications.")
        Text("Step 9: Add the recommended amount and type of engine oil through the oil fill cap.")
        Text("Step 10: Start the engine and allow it to idle for two minutes.")
        Text("Step 11: Check for leaks around the drain plug and oil filter.")
        Text("Step 12: Turn the engine off and verify the oil level using the dipstick.")
        Text("Step 13: Reset the oil-life monitor or maintenance reminder if applicable.")
        Text("Step 14: Properly recycle the used oil and oil filter.")

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Reminder: Always consult the vehicle owner's manual for the correct oil type, oil capacity, filter specifications, and maintenance interval.")

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = onBack){
            Text("Back")
        }
    }
}

