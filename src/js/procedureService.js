// Retrieves step-by-step maintenance procedures.
// Procedure data comes only from src/data/procedures.json. This service does not
// create, modify, or hard-code maintenance procedures.

const PROCEDURES_DATA_URL = new URL("../data/procedures.json", import.meta.url);
let proceduresDataPromise = null;

// Returns only valid procedure objects from a JSON array or common object wrapper.
function getProcedureList(data) {
	if (!data || typeof data !== "object") {
		return [];
	}

	const candidates = [data.procedures, data.items, data.jobs];
	for (const candidate of candidates) {
		if (Array.isArray(candidate)) {
			return candidate.filter((procedure) => procedure && typeof procedure === "object");
		}
	}

	return Array.isArray(data) ? data.filter((procedure) => procedure && typeof procedure === "object") : [];
}

// Loads the procedure JSON once so later calls reuse the same request.
async function loadProcedures() {
	if (proceduresDataPromise) {
		return proceduresDataPromise;
	}

	proceduresDataPromise = fetch(PROCEDURES_DATA_URL)
		.then((response) => {
			if (!response.ok) {
				throw new Error(`Could not load procedures (${response.status}).`);
			}

			return response.json();
		})
		.catch(() => null);

	return proceduresDataPromise;
}

// Accepts a procedure ID or a procedure-like object. Invalid values return null.
function resolveProcedure(procedureOrId) {
	if (!procedureOrId || typeof procedureOrId !== "object" && typeof procedureOrId !== "string") {
		return null;
	}

	if (typeof procedureOrId === "string") {
		return procedureOrId.trim() === "" ? null : procedureOrId;
	}

	return procedureOrId.id ?? procedureOrId.procedureId ?? null;
}

// Returns an array when a JSON field contains an array. Missing or invalid
// fields produce an empty array instead of causing an error.
function getArray(value) {
	return Array.isArray(value) ? value : [];
}

// Returns all procedures from procedures.json. The current JSON is empty, so
// this function returns an empty array until procedures are added.
export async function getAllProcedures() {
	const data = await loadProcedures();
	return getProcedureList(data);
}

// Finds a procedure by its ID. Invalid IDs or missing procedures return null.
export async function getProcedureById(id) {
	if (typeof id !== "string" || id.trim() === "") {
		return null;
	}

	const procedures = await getAllProcedures();
	return procedures.find((procedure) => procedure.id === id.trim()) ?? null;
}

// Finds the procedure connected to a maintenance item. It checks common
// procedure reference fields without hard-coding any procedure IDs.
export async function getProcedureForMaintenanceItem(maintenanceItem) {
	if (!maintenanceItem || typeof maintenanceItem !== "object") {
		return null;
	}

	const procedures = await getAllProcedures();
	const references = [
		maintenanceItem.procedureId,
		maintenanceItem.procedure?.id,
		maintenanceItem.procedureId,
		maintenanceItem.id
	];

	for (const reference of references) {
		if (typeof reference !== "string" || reference.trim() === "") {
			continue;
		}

		const procedure = procedures.find((item) => item.id === reference.trim());
		if (procedure) {
			return procedure;
		}
	}

	return null;
}

// Returns the tools listed for a procedure. Supported fields are tools,
// requiredTools, equipment, and requiredEquipment.
export async function getProcedureTools(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return [];
	}

	return getArray(
		procedure.tools ??
			procedure.requiredTools ??
			procedure.equipment ??
			procedure.requiredEquipment
	);
}

// Returns PPE and safety equipment from the procedure. Missing fields return [].
export async function getRequiredPpe(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return [];
	}

	return getArray(
		procedure.ppe ??
			procedure.requiredPpe ??
			procedure.safetyEquipment ??
			procedure.requiredSafetyEquipment
	);
}

// Returns preparation instructions from the procedure. The value is returned
// as an array when present, or as an empty array when missing.
export async function getPreparationInstructions(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return [];
	}

	return getArray(
		procedure.preparation ??
			procedure.prep ??
			procedure.preparationInstructions
	);
}

// Preserves the procedure's step order and returns an empty array if no steps
// are provided.
export async function getProcedureSteps(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return [];
	}

	return getArray(procedure.steps ?? procedure.orderedSteps);
}

// Returns completion and final-check instructions in their original order.
export async function getCompletionChecks(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return [];
	}

	return getArray(
		procedure.completionChecklist ??
			procedure.completionChecks ??
			procedure.finalChecks ??
			procedure.completionInstructions
	);
}

// Combines the procedure's useful information into a simple summary object.
export async function getProcedureSummary(procedureOrId) {
	const procedure = typeof procedureOrId === "string" || procedureOrId?.id
		? await getProcedureById(resolveProcedure(procedureOrId))
		: procedureOrId;

	if (!procedure) {
		return null;
	}

	return {
		id: procedure.id ?? null,
		name: procedure.name ?? procedure.title ?? null,
		description: procedure.description ?? null,
		estimatedTime: procedure.estimatedTime ?? procedure.estimatedMinutes ?? null,
		difficulty: procedure.difficulty ?? null,
		tools: await getProcedureTools(procedure),
		ppe: await getRequiredPpe(procedure),
		preparation: await getPreparationInstructions(procedure),
		steps: await getProcedureSteps(procedure),
		completionChecks: await getCompletionChecks(procedure)
	};
}

// Helpful aliases for callers that prefer shorter names.
export const getAllItems = getAllProcedures;
export const findProcedureById = getProcedureById;
export const getTools = getProcedureTools;
export const getPpe = getRequiredPpe;
export const getPreparation = getPreparationInstructions;
export const getSteps = getProcedureSteps;
export const getCompletion = getCompletionChecks;
