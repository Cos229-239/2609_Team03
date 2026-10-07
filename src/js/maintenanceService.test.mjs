import assert from "node:assert/strict";
import { after, test } from "node:test";

import {
	getAllMaintenance,
	getMaintenanceById,
	getDueMaintenance,
	getUpcomingMaintenance,
	getMaintenanceCategories
} from "./maintenanceService.js";

const maintenanceItems = [
	{ id: "oil-change", name: "Oil change", category: "Engine", dueMileage: 49000 },
	{ id: "brake-inspection", name: "Brake inspection", category: "Brakes", dueMileage: 50000 },
	{ id: "tire-rotation", name: "Tire rotation", category: "Tires", dueMileage: 50750 },
	{ id: "coolant-service", name: "Coolant service", category: "Engine", dueMileage: 52001 }
];

// Supply predictable maintenance records without changing maintenance.json.
const originalFetch = globalThis.fetch;
globalThis.fetch = async () => ({
	ok: true,
	json: async () => ({ maintenanceItems })
});

after(() => {
	if (originalFetch) {
		globalThis.fetch = originalFetch;
	} else {
		delete globalThis.fetch;
	}
});

// Check that the service loads and returns all maintenance records.
test("getAllMaintenance returns all maintenance items", async () => {
	assert.deepEqual(await getAllMaintenance(), maintenanceItems);
});

// Check that an item can be found by ID and that missing IDs return null.
test("getMaintenanceById finds an item by ID", async () => {
	assert.deepEqual(await getMaintenanceById("tire-rotation"), maintenanceItems[2]);
	assert.equal(await getMaintenanceById("missing-item"), null);
});

// Check due results, including mileage strings, vehicle objects, and invalid input.
test("getDueMaintenance handles mileage input", async () => {
	assert.deepEqual(
		(await getDueMaintenance("50000")).map((item) => item.id),
		["oil-change", "brake-inspection"]
	);
	assert.deepEqual(
		(await getDueMaintenance({ currentMileage: 50000 })).map((item) => item.id),
		["oil-change", "brake-inspection"]
	);
	assert.deepEqual(await getDueMaintenance("-1"), []);
});

// Check that only items within the warning distance are upcoming.
test("getUpcomingMaintenance returns items due soon", async () => {
	assert.deepEqual(
		(await getUpcomingMaintenance(50000, 1000)).map((item) => item.id),
		["tire-rotation"]
	);
	assert.deepEqual(await getUpcomingMaintenance("not a mileage"), []);
});

// Check that maintenance is separated into due, due-soon, and not-due groups.
test("getMaintenanceCategories groups items by status", async () => {
	const categories = await getMaintenanceCategories(50000, 1000);

	assert.deepEqual(categories.due.map((item) => item.id), [
		"oil-change",
		"brake-inspection"
	]);
	assert.deepEqual(categories.dueSoon.map((item) => item.id), ["tire-rotation"]);
	assert.deepEqual(categories.notDue.map((item) => item.id), ["coolant-service"]);
});
