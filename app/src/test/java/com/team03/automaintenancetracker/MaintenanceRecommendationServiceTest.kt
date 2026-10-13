package com.team03.automaintenancetracker

import com.team03.automaintenancetracker.data.MaintenanceItem
import com.team03.automaintenancetracker.data.MaintenanceRecommendationService
import com.team03.automaintenancetracker.data.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test

class MaintenanceRecommendationServiceTest {
    @Test
    fun groupsItemsByTheVehicleMileage() {
        val vehicle = Vehicle("saved-id", 2018, "Honda", "Civic", 50000)
        val items = listOf(
            MaintenanceItem("due", "Oil and filter", "Engine", "miles", 45000, 5000, null),
            MaintenanceItem("upcoming", "Tire rotation", "Tires", "miles", 45000, 50750 - 45000, null),
            MaintenanceItem("not-due", "Transmission fluid", "Transmission", "miles", 30000, 30000, null)
        )

        val result = MaintenanceRecommendationService.getVehicleMaintenance(vehicle, items)

        assertEquals(listOf("due"), result.due.map { it.item.id })
        assertEquals(listOf("upcoming"), result.dueSoon.map { it.item.id })
        assertEquals(listOf("not-due"), result.notDue.map { it.item.id })
    }

    @Test
    fun returnsEmptyDueAndUpcomingListsWhenNothingIsInRange() {
        val vehicle = Vehicle("saved-id", 2018, "Honda", "Civic", 50000)
        val items = listOf(
            MaintenanceItem("later", "Transmission fluid", "Transmission", "miles", 30000, 30000, null)
        )

        val result = MaintenanceRecommendationService.getVehicleMaintenance(vehicle, items)

        assertEquals(emptyList<String>(), result.due.map { it.item.id })
        assertEquals(emptyList<String>(), result.dueSoon.map { it.item.id })
        assertEquals(listOf("later"), result.notDue.map { it.item.id })
    }
}
