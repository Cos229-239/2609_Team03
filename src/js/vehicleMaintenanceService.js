import { getDueMaintenance, getUpcomingMaintenance } from "./maintenanceService.js";
import { getVehicleById } from "./vehicleService.js";

// Looks up a saved vehicle and passes its mileage to the maintenance service.
export async function getVehicleMaintenance(vehicleId, warningDistance) {
	const vehicle = getVehicleById(vehicleId);
	if (!vehicle) {
		return null;
	}

	const due = await getDueMaintenance(vehicle.mileage);
	const upcoming = await getUpcomingMaintenance(vehicle.mileage, warningDistance);

	return { vehicle, due, upcoming };
}
