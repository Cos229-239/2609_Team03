package com.team03.automaintenancetracker.data

import android.content.Context
import org.json.JSONArray
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
    val dueSoon: List<ScheduledMaintenance>,
    val notDue: List<ScheduledMaintenance>,
    val errorMessage: String? = null
) {
    val upcoming: List<ScheduledMaintenance>
        get() = dueSoon
}

object MaintenanceRecommendationService {
    /** Maintenance is due soon when it is within 1,000 odometer units of its service mileage. */
    const val DUE_SOON_DISTANCE = 1000

    fun getVehicleMaintenance(
        context: Context,
        vehicle: Vehicle,
        warningDistance: Int = DUE_SOON_DISTANCE
    ): VehicleMaintenance {
        if (vehicle.mileage < 0) {
            return VehicleMaintenance(
                vehicle = vehicle,
                due = emptyList(),
                dueSoon = emptyList(),
                notDue = emptyList(),
                errorMessage = "Vehicle mileage must be zero or greater."
            )
        }

        val data = JSONObject(context.assets.open("maintenance.json").bufferedReader().use { it.readText() })
        val items = (data.optJSONArray("maintenanceItems") ?: JSONArray())
            .toObjectList()
            .map { json ->
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

    /**
     * The data asset supplies prototype service anchors and intervals; the vehicle's current
     * mileage determines whether each next service milestone is due, due soon, or not due.
     * MaintenanceRecord is not persisted or linked to a vehicle/item yet, so history cannot
     * safely replace those prototype anchors.
     */
    fun getVehicleMaintenance(
        vehicle: Vehicle,
        items: List<MaintenanceItem>,
        warningDistance: Int = DUE_SOON_DISTANCE
    ): VehicleMaintenance {
        if (vehicle.mileage < 0) {
            return VehicleMaintenance(
                vehicle = vehicle,
                due = emptyList(),
                dueSoon = emptyList(),
                notDue = emptyList(),
                errorMessage = "Vehicle mileage must be zero or greater."
            )
        }

        val scheduled = items.mapNotNull { item ->
            item.nextDueMileage()?.let { ScheduledMaintenance(item, it) }
        }
        val due = scheduled.filter { it.dueMileage <= vehicle.mileage }
        val dueSoonDistance = warningDistance.coerceAtLeast(0)
        val dueSoon = scheduled.filter {
            val distance = it.dueMileage - vehicle.mileage
            distance in 1..dueSoonDistance
        }
        val notDue = scheduled.filter {
            it.dueMileage - vehicle.mileage > dueSoonDistance
        }
        return VehicleMaintenance(vehicle, due, dueSoon, notDue)
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
