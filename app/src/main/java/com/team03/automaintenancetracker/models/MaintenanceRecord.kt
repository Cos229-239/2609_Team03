package com.team03.automaintenancetracker.models

data class MaintenanceRecord(
    val maintenanceType: String,
    val mileage: Int,
    val completedDate: String,
    val notes: String
)