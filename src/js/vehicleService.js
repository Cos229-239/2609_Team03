const VEHICLES_STORAGE_KEY = "automotiveMaintenanceVehicles";

// Reads the saved vehicle list from the browser's localStorage.
function loadVehicles() {
	const savedVehicles = localStorage.getItem(VEHICLES_STORAGE_KEY);

	if (savedVehicles === null) {
		return [];
	}

	let vehicles;
	try {
		vehicles = JSON.parse(savedVehicles);
	} catch {
		throw new Error("Saved vehicle data is not valid JSON.");
	}

	if (!Array.isArray(vehicles)) {
		throw new Error("Saved vehicle data must be a list.");
	}

	return vehicles;
}

// Saves the complete vehicle list so it is available after the page reloads.
function saveVehicles(vehicles) {
	localStorage.setItem(VEHICLES_STORAGE_KEY, JSON.stringify(vehicles));
}

// Checks and converts a year before it is saved.
function validateYear(year) {
	if ((typeof year !== "number" && typeof year !== "string") || String(year).trim() === "") {
		throw new Error("Year must be a valid number.");
	}

	const numericYear = Number(year);
	const latestAllowedYear = new Date().getFullYear() + 1;

	if (!Number.isInteger(numericYear) || numericYear < 1886 || numericYear > latestAllowedYear) {
		throw new Error(`Year must be between 1886 and ${latestAllowedYear}.`);
	}

	return numericYear;
}

// Checks and converts mileage before it is saved.
function validateMileage(mileage) {
	if ((typeof mileage !== "number" && typeof mileage !== "string") || String(mileage).trim() === "") {
		throw new Error("Mileage must be a valid number.");
	}

	const numericMileage = Number(mileage);
	if (!Number.isInteger(numericMileage) || numericMileage < 0) {
		throw new Error("Mileage must be a non-negative whole number.");
	}

	return numericMileage;
}

// Creates an ID that does not already belong to a saved vehicle.
function createUniqueId(vehicles) {
	let id;

	do {
		if (globalThis.crypto && typeof globalThis.crypto.randomUUID === "function") {
			id = globalThis.crypto.randomUUID();
		} else {
			id = `vehicle-${Date.now()}-${Math.random().toString(36).slice(2)}`;
		}
	} while (vehicles.some((vehicle) => vehicle.id === id));

	return id;
}

// Validates and saves a new vehicle, then returns the saved vehicle.
export function addVehicle(vehicleDetails) {
	if (!vehicleDetails || typeof vehicleDetails !== "object" || Array.isArray(vehicleDetails)) {
		throw new Error("Vehicle details must be provided as an object.");
	}

	if (typeof vehicleDetails.make !== "string" || vehicleDetails.make.trim() === "") {
		throw new Error("Vehicle make is required.");
	}

	if (typeof vehicleDetails.model !== "string" || vehicleDetails.model.trim() === "") {
		throw new Error("Vehicle model is required.");
	}

	if (vehicleDetails.nickname !== undefined && vehicleDetails.nickname !== null && typeof vehicleDetails.nickname !== "string") {
		throw new Error("Vehicle nickname must be text.");
	}

	const vehicles = loadVehicles();
	const nickname = vehicleDetails.nickname ? vehicleDetails.nickname.trim() : "";
	const newVehicle = {
		id: createUniqueId(vehicles),
		year: validateYear(vehicleDetails.year),
		make: vehicleDetails.make.trim(),
		model: vehicleDetails.model.trim(),
		mileage: validateMileage(vehicleDetails.mileage),
		dateAdded: new Date().toISOString()
	};

	if (nickname) {
		newVehicle.nickname = nickname;
	}

	vehicles.push(newVehicle);
	saveVehicles(vehicles);
	return newVehicle;
}

// Returns every vehicle currently saved in localStorage.
export function getAllVehicles() {
	return loadVehicles();
}

// Finds one vehicle by its ID, or returns null when it is not found.
export function getVehicleById(id) {
	if (typeof id !== "string") {
		return null;
	}

	return loadVehicles().find((vehicle) => vehicle.id === id) ?? null;
}

// Updates a vehicle's mileage and returns it, or null if its ID is not found.
export function updateVehicleMileage(id, mileage) {
	const vehicles = loadVehicles();
	const vehicle = vehicles.find((savedVehicle) => savedVehicle.id === id);

	if (!vehicle) {
		return null;
	}

	vehicle.mileage = validateMileage(mileage);
	saveVehicles(vehicles);
	return vehicle;
}

// Deletes a vehicle by ID and reports whether a vehicle was removed.
export function deleteVehicle(id) {
	const vehicles = loadVehicles();
	const vehicleIndex = vehicles.findIndex((vehicle) => vehicle.id === id);

	if (vehicleIndex === -1) {
		return false;
	}

	vehicles.splice(vehicleIndex, 1);
	saveVehicles(vehicles);
	return true;
}
