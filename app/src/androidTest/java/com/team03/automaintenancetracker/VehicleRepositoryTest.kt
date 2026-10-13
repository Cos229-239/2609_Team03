package com.team03.automaintenancetracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.team03.automaintenancetracker.data.MaintenanceRecommendationService
import com.team03.automaintenancetracker.data.Vehicle
import com.team03.automaintenancetracker.data.VehicleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
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
    fun savesNewVehicleAndUsesItsMileageForRecommendations() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val preferences = context.getSharedPreferences("vehicle_repository", android.content.Context.MODE_PRIVATE)
        val originalVehicles = preferences.getString("vehicles", null)

        try {
            preferences.edit().remove("vehicles").commit()
            val savedVehicle = VehicleRepository.saveVehicle(
                context,
                Vehicle("", 2020, "Example", "Car", 50000)
            )

            assertFalse(savedVehicle.id.isBlank())
            assertTrue(VehicleRepository.loadVehicles(context).contains(savedVehicle))

            val recommendations =
                MaintenanceRecommendationService.getVehicleMaintenance(context, savedVehicle)
            assertEquals(50000, recommendations.vehicle.mileage)
            assertTrue(recommendations.due.any { it.item.id == "engine-oil-change" })
            assertTrue(recommendations.notDue.any { it.item.id == "tire-rotation" })
        } finally {
            val editor = preferences.edit()
            if (originalVehicles == null) {
                editor.remove("vehicles")
            } else {
                editor.putString("vehicles", originalVehicles)
            }
            editor.commit()
        }
    }

    @Test
    fun rejectsInvalidVehicleInformation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        assertInvalidVehicle(context, Vehicle("", 2020, " ", "Car", 0))
        assertInvalidVehicle(context, Vehicle("", 2020, "Example", " ", 0))
        assertInvalidVehicle(context, Vehicle("", 2020, "Example", "Car", -1))
        assertInvalidVehicle(context, Vehicle("", 0, "Example", "Car", 0))
    }

    @Test
    fun loadsEmptyVehicleAssetAsNoVehicles() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        assertEquals(emptyList<Vehicle>(), VehicleRepository.loadVehicles(context))
    }

    private fun assertInvalidVehicle(
        context: android.content.Context,
        vehicle: Vehicle
    ) {
        try {
            VehicleRepository.saveVehicle(context, vehicle)
            fail("Expected invalid vehicle data to be rejected.")
        } catch (_: IllegalArgumentException) {
            // Expected validation failure.
        }
    }
}
