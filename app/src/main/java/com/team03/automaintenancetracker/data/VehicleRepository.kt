package com.team03.automaintenancetracker.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Vehicle(
    val id: String,
    val year: Int,
    val make: String,
    val model: String,
    val mileage: Int
)

object VehicleRepository {
    fun loadVehicles(context: Context): List<Vehicle> {
        val json = context.assets.open("vehicles.json").bufferedReader().use { it.readText() }
        return parseVehicles(json)
    }

    fun parseVehicles(json: String): List<Vehicle> =
        JSONArray(json).toObjectList().map { vehicle ->
            val id = vehicle.requiredString("id")
            val year = vehicle.nonNegativeInt("year")
                ?: throw IllegalArgumentException("Vehicle '$id' has an invalid year.")
            val mileage = vehicle.nonNegativeInt("mileage")
                ?: throw IllegalArgumentException("Vehicle '$id' has invalid mileage.")

            Vehicle(
                id = id,
                year = year,
                make = vehicle.requiredString("make"),
                model = vehicle.requiredString("model"),
                mileage = mileage
            )
        }

    private fun JSONArray.toObjectList(): List<JSONObject> =
        (0 until length()).map { getJSONObject(it) }

    private fun JSONObject.requiredString(key: String): String =
        optString(key).takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("Required text field '$key' is missing.")

    private fun JSONObject.nonNegativeInt(key: String): Int? {
        if (!has(key) || isNull(key)) return null
        val value = optDouble(key, Double.NaN)
        return value.takeIf {
            it.isFinite() && it >= 0 && it <= Int.MAX_VALUE && it % 1.0 == 0.0
        }?.toInt()
    }
}
