package com.team03.automaintenancetracker.models

data class Vehicle(
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val vin: String
)