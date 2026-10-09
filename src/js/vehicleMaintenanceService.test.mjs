import assert from "node:assert/strict";
import { after, test } from "node:test";

import { getVehicleMaintenance } from "./vehicleMaintenanceService.js";

const vehicle = {
	id: "saved-vehicle",
	year: 2018,
	make: "Honda",
	model: "Civic",
	mileage: 50000
};
const maintenanceItems = [
	{ id: "oil-change", name: "Oil change", dueMileage: 50000 },
	{ id: "tire-rotation", name: "Tire rotation", dueMileage: 50750 }
];

const originalFetch = globalThis.fetch;
const originalLocalStorage = Object.getOwnPropertyDescriptor(globalThis, "localStorage");
globalThis.fetch = async () => ({
	ok: true,
	json: async () => ({ maintenanceItems })
});
Object.defineProperty(globalThis, "localStorage", {
	configurable: true,
	value: {
		getItem: (key) => key === "automotiveMaintenanceVehicles" ? JSON.stringify([vehicle]) : null
	}
});

after(() => {
	if (originalFetch) {
		globalThis.fetch = originalFetch;
	} else {
		delete globalThis.fetch;
	}

	if (originalLocalStorage) {
		Object.defineProperty(globalThis, "localStorage", originalLocalStorage);
	} else {
		delete globalThis.localStorage;
	}
});

test("getVehicleMaintenance uses stored vehicle mileage for due and upcoming items", async () => {
	const result = await getVehicleMaintenance(vehicle.id, 1000);

	assert.deepEqual(result.vehicle, vehicle);
	assert.deepEqual(result.due.map((item) => item.id), ["oil-change"]);
	assert.deepEqual(result.upcoming.map((item) => item.id), ["tire-rotation"]);
});

test("getVehicleMaintenance returns null when the vehicle ID is not found", async () => {
	assert.equal(await getVehicleMaintenance("missing-vehicle"), null);
});
