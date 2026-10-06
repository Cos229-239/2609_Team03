// Determines maintenance recommendations and upcoming maintenance.
// All maintenance data comes from src/data/maintenance.json; this service does
// not create or hard-code maintenance jobs.

const MAINTENANCE_DATA_URL = new URL("../data/maintenance.json", import.meta.url);
const DEFAULT_WARNING_DISTANCE = 1000;

let maintenanceDataPromise = null;

// Converts a value to a non-negative whole number of miles. Invalid values are
// rejected so that maintenance calculations never crash the application.
function normalizeMileage(value) {
	if (typeof value === "number") {
		return Number.isFinite(value) && Number.isInteger(value) && value >= 0 ? value : null;
	}

	if (typeof value === "string" && value.trim() !== "") {
		const numberValue = Number(value);
		return Number.isFinite(numberValue) && Number.isInteger(numberValue) && numberValue >= 0
			? numberValue
			: null;
	}

	return null;
}

// Extracts the mileage from a vehicle object, while also accepting a plain
// mileage number for simpler callers.
function getVehicleMileage(vehicleOrMileage) {
	if (typeof vehicleOrMileage === "number" || typeof vehicleOrMileage === "string") {
		return normalizeMileage(vehicleOrMileage);
	}

	if (!vehicleOrMileage || typeof vehicleOrMileage !== "object") {
		return null;
	}

	return normalizeMileage(
		vehicleOrMileage.mileage ??
			vehicleOrMileage.currentMileage ??
			vehicleOrMileage.odometer
	);
}

// Uses a safe fallback if the JSON file cannot be loaded or is malformed.
async function loadMaintenanceData() {
	if (maintenanceDataPromise) {
		return maintenanceDataPromise;
	}

	maintenanceDataPromise = fetch(MAINTENANCE_DATA_URL)
		.then((response) => {
			if (!response.ok) {
				throw new Error(`Could not load maintenance data (${response.status}).`);
			}

			return response.json();
		})
		.catch(() => null);

	return maintenanceDataPromise;
}

// Look for a maintenance item list in common, readable locations in the JSON.
function getMaintenanceItems(data) {
	if (!data || typeof data !== "object") {
		return [];
	}

	const candidates = [
		data.maintenanceItems,
		data.items,
		data.maintenance,
		data.jobs
	];

	for (const candidate of candidates) {
		if (Array.isArray(candidate)) {
			return candidate.filter((item) => item && typeof item === "object");
		}
	}

	return [];
}

// Returns a normalized interval in the same unit as the vehicle mileage.
function getMaintenanceInterval(item) {
	const unit = String(item.mileageUnit ?? item.unit ?? "miles").toLowerCase();
	const interval = unit === "kilometers" || unit === "km"
		? item.intervalKm ?? item.intervalKilometers ?? item.interval
		: item.intervalMiles ?? item.intervalMileage ?? item.interval;

	return normalizeMileage(interval);
}

// Finds the last service mileage in the same unit as the interval.
function getLastServiceMileage(item) {
	return normalizeMileage(
		item.lastServiceMileage ??
			item.mileageAtLastService ??
			item.lastMileage ??
			item.serviceMileage
	);
}

// Calculates the next due mileage using the stored interval. A direct
// dueMileage value is preferred when the data already provides it.
function getDueMileage(item) {
	const directDueMileage = normalizeMileage(
		item.dueMileage ?? item.nextServiceMileage ?? item.mileageDue
	);

	if (directDueMileage !== null) {
		return directDueMileage;
	}

	const lastServiceMileage = getLastServiceMileage(item);
	const interval = getMaintenanceInterval(item);

	if (lastServiceMileage === null || interval === null) {
		return null;
	}

	return lastServiceMileage + interval;
}

// Returns the effective due distance in the same unit as the current mileage.
function getDistanceUntilDue(item, currentMileage) {
	const dueMileage = getDueMileage(item);

	if (dueMileage === null || currentMileage === null) {
		return null;
	}

	return dueMileage - currentMileage;
}

// Returns all maintenance items from the data file. An empty array is returned
// when the file is unavailable or does not contain an item list.
export async function getAllMaintenance() {
	const data = await loadMaintenanceData();
	return getMaintenanceItems(data);
}

// Finds one maintenance item by ID, or returns null when it is not found.
export async function getMaintenanceById(id) {
	if (typeof id !== "string" || id.trim() === "") {
		return null;
	}

	const items = await getAllMaintenance();
	return items.find((item) => item.id === id) ?? null;
}

// Returns maintenance items that are due at or before the current mileage.
export async function getDueMaintenance(vehicleOrMileage) {
	const currentMileage = getVehicleMileage(vehicleOrMileage);
	if (currentMileage === null) {
		return [];
	}

	const items = await getAllMaintenance();
	return items.filter((item) => {
		const distance = getDistanceUntilDue(item, currentMileage);
		return distance !== null && distance <= 0;
	});
}

// Returns items that are close to their due mileage. The warning distance can
// be supplied by the caller, or defaults to 1,000 miles.
export async function getUpcomingMaintenance(vehicleOrMileage, warningDistance = DEFAULT_WARNING_DISTANCE) {
	const currentMileage = getVehicleMileage(vehicleOrMileage);
	const normalizedWarningDistance = normalizeMileage(warningDistance);

	if (currentMileage === null || normalizedWarningDistance === null) {
		return [];
	}

	const items = await getAllMaintenance();
	return items.filter((item) => {
		const distance = getDistanceUntilDue(item, currentMileage);
		return distance !== null && distance > 0 && distance <= normalizedWarningDistance;
	});
}

// Separates items into useful categories. The returned objects are new arrays
// and contain the original item data without changing the source JSON.
export async function getMaintenanceCategories(vehicleOrMileage, warningDistance = DEFAULT_WARNING_DISTANCE) {
	const currentMileage = getVehicleMileage(vehicleOrMileage);
	if (currentMileage === null) {
		return {
			due: [],
			dueSoon: [],
			notDue: []
		};
	}

	const allItems = await getAllMaintenance();
	const due = await getDueMaintenance(currentMileage);
	const dueSoon = await getUpcomingMaintenance(currentMileage, warningDistance);
	const dueIds = new Set(due.map((item) => item.id));
	const dueSoonIds = new Set(dueSoon.map((item) => item.id));
	const notDue = allItems.filter((item) =>
		!dueIds.has(item.id) && !dueSoonIds.has(item.id)
	);

	return { due, dueSoon, notDue };
}

// Short aliases make the service easy to use while keeping the public API clear.
export const getAllItems = getAllMaintenance;
export const findMaintenanceById = getMaintenanceById;
export const getDueItems = getDueMaintenance;
export const getUpcomingItems = getUpcomingMaintenance;
export const categorizeMaintenance = getMaintenanceCategories;
