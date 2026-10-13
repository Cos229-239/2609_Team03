package com.team03.automaintenancetracker.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID

data class Vehicle(
    val id: String,
    val year: Int,
    val make: String,
    val model: String,
    val mileage: Int,
    val vin: String = ""
)

object VehicleRepository {
    private const val PREFERENCES_NAME = "vehicle_repository"
    private const val VEHICLES_KEY = "vehicles"
    private const val MINIMUM_VEHICLE_YEAR = 1886

    fun loadVehicles(context: Context): List<Vehicle> {
        val assetJson = context.assets.open("vehicles.json").bufferedReader().use { it.readText() }
        val assetVehicles = parseVehicles(assetJson)
        val savedJson = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(VEHICLES_KEY, null)
            ?: return assetVehicles
        return mergeVehicles(assetVehicles, parseVehicles(savedJson))
    }

    fun saveVehicle(context: Context, vehicle: Vehicle): Vehicle {
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        require(vehicle.year in MINIMUM_VEHICLE_YEAR..(currentYear + 1)) {
            "Enter a vehicle year from $MINIMUM_VEHICLE_YEAR to ${currentYear + 1}."
        }
        require(vehicle.mileage >= 0) { "Mileage cannot be negative." }
        require(vehicle.make.isNotBlank()) { "Make cannot be blank." }
        require(vehicle.model.isNotBlank()) { "Model cannot be blank." }

        val existingVehicles = loadVehicles(context)
        val savedVehicle = vehicle.copy(
            id = vehicle.id.ifBlank { UUID.randomUUID().toString() },
            make = vehicle.make.trim(),
            model = vehicle.model.trim(),
            vin = vehicle.vin.trim()
        )
        val updatedVehicles = mergeVehicles(existingVehicles, listOf(savedVehicle))
        val saved = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(VEHICLES_KEY, updatedVehicles.toJson().toString())
            .commit()
        if (!saved) {
            throw IOException("The vehicle could not be saved to device storage.")
        }
        return savedVehicle
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
                mileage = mileage,
                vin = vehicle.optString("vin")
            )
        }

    private fun mergeVehicles(existing: List<Vehicle>, updates: List<Vehicle>): List<Vehicle> {
        val vehiclesById = LinkedHashMap<String, Vehicle>()
        existing.forEach { vehiclesById[it.id] = it }
        updates.forEach { vehiclesById[it.id] = it }
        return vehiclesById.values.toList()
    }

    private fun List<Vehicle>.toJson(): JSONArray =
        JSONArray().also { vehicles ->
            forEach { vehicle ->
                vehicles.put(
                    JSONObject()
                        .put("id", vehicle.id)
                        .put("year", vehicle.year)
                        .put("make", vehicle.make)
                        .put("model", vehicle.model)
                        .put("mileage", vehicle.mileage)
                        .put("vin", vehicle.vin)
                )
            }
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
