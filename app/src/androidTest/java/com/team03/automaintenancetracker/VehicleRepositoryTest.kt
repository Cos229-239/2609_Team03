package com.team03.automaintenancetracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.team03.automaintenancetracker.data.MaintenanceRecommendationService
import com.team03.automaintenancetracker.data.Vehicle
import com.team03.automaintenancetracker.data.VehicleRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VehicleRepositoryTest {
    @Test
    fun parsesEmptyVehicleData() {
        assertEquals(emptyList<Vehicle>(), VehicleRepository.parseVehicles("[]"))
    }

    @Test
    fun parsesVehicleJson() {
        val vehicles = VehicleRepository.parseVehicles(
            """[{"id":"test-vehicle","year":2018,"make":"Honda","model":"Civic","mileage":50000}]"""
        )

        assertEquals(
            listOf(Vehicle("test-vehicle", 2018, "Honda", "Civic", 50000)),
            vehicles
        )
    }

    @Test
    fun selectedVehicleMileageIsUsedForRecommendations() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val vehicle = Vehicle("test-vehicle", 2018, "Honda", "Civic", 50000)

        val result = MaintenanceRecommendationService.getVehicleMaintenance(context, vehicle, warningDistance = 3000)

        assertEquals(vehicle, result.vehicle)
        assertEquals(
            listOf("engine-oil-change", "engine-air-filter", "brake-inspection"),
            result.due.map { it.item.id }
        )
        assertEquals(listOf("tire-rotation"), result.upcoming.map { it.item.id })
    }

    @Test
    fun loadsEmptyVehicleAssetAsNoVehicles() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals(emptyList<Vehicle>(), VehicleRepository.loadVehicles(context))
    }
}
