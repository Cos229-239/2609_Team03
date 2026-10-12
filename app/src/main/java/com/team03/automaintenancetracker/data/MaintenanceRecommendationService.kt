package com.team03.automaintenancetracker.data

import android.content.Context
import org.json.JSONObject

data class MaintenanceItem(
    val id: String,
    val name: String,
    val category: String?,
    val mileageUnit: String,
    val lastServiceMileage: Int?,
    val interval: Int?,
    val dueMileage: Int?
) {
    fun nextDueMileage(): Int? {
        if (dueMileage != null) return dueMileage
        if (lastServiceMileage == null || interval == null) return null
        return (lastServiceMileage.toLong() + interval).takeIf { it <= Int.MAX_VALUE }?.toInt()
    }
}

data class ScheduledMaintenance(
    val item: MaintenanceItem,
    val dueMileage: Int
)

data class VehicleMaintenance(
    val vehicle: Vehicle,
    val due: List<ScheduledMaintenance>,
    val upcoming: List<ScheduledMaintenance>
)

object MaintenanceRecommendationService {
    private const val WARNING_DISTANCE = 1000

    fun getVehicleMaintenance(
        context: Context,
        vehicle: Vehicle,
        warningDistance: Int = WARNING_DISTANCE
    ): VehicleMaintenance {
        val data = JSONObject(context.assets.open("maintenance.json").bufferedReader().use { it.readText() })
        val items = data.getJSONArray("maintenanceItems").toObjectList().map { json ->
            val id = json.requiredString("id")
            val name = json.requiredString("name")
            val mileageUnit = json.optString("mileageUnit", "miles").lowercase()
            val interval = if (mileageUnit == "km" || mileageUnit == "kilometers") {
                json.nonNegativeInt("intervalKm")
                    ?: json.nonNegativeInt("intervalKilometers")
                    ?: json.nonNegativeInt("interval")
            } else {
                json.nonNegativeInt("intervalMiles")
                    ?: json.nonNegativeInt("intervalMileage")
                    ?: json.nonNegativeInt("interval")
            }
            val lastServiceMileage = json.nonNegativeInt("lastServiceMileage")
                ?: json.nonNegativeInt("mileageAtLastService")
                ?: json.nonNegativeInt("lastMileage")
                ?: json.nonNegativeInt("serviceMileage")
            val dueMileage = json.nonNegativeInt("dueMileage")
                ?: json.nonNegativeInt("nextServiceMileage")
                ?: json.nonNegativeInt("mileageDue")

            if (dueMileage == null && (lastServiceMileage == null || interval == null)) {
                throw IllegalArgumentException("Maintenance item '$id' is missing mileage information.")
            }

            MaintenanceItem(
                id = id,
                name = name,
                category = json.optString("category").takeIf { it.isNotBlank() },
                mileageUnit = mileageUnit,
                lastServiceMileage = lastServiceMileage,
                interval = interval,
                dueMileage = dueMileage
            )
        }

        return getVehicleMaintenance(vehicle, items, warningDistance)
    }

    // Apply the same due and 1,000-mile upcoming windows used by the JS service.
    fun getVehicleMaintenance(
        vehicle: Vehicle,
        items: List<MaintenanceItem>,
        warningDistance: Int = WARNING_DISTANCE
    ): VehicleMaintenance {
        val scheduled = items.mapNotNull { item ->
            item.nextDueMileage()?.let { ScheduledMaintenance(item, it) }
        }
        val due = scheduled.filter { it.dueMileage <= vehicle.mileage }
        val upcoming = scheduled.filter {
            val distance = it.dueMileage - vehicle.mileage
            distance in 1..warningDistance
        }
        return VehicleMaintenance(vehicle, due, upcoming)
    }

    private fun org.json.JSONArray.toObjectList(): List<JSONObject> =
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
