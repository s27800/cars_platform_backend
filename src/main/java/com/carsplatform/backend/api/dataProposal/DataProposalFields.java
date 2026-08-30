package com.carsplatform.backend.api.dataProposal;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Fields a user is allowed to propose a change for (for each category).
 */
public final class DataProposalFields {

    private static final Map<String, Set<String>> ALLOWED_FIELDS = Map.of(
            "ENGINE", Set.of(
                    "engineCode", "displacement", "engineType", "maxPower", "maxPowerRotationSpeed",
                    "maxTorque", "maxTorqueRotationSpeed", "cylindersNumber", "valvesNumber", "turbo"),

            "TRANSMISSION", Set.of(
                    "transmissionType", "transmissionName", "gearsNumber", "clutchType"),

            "CHASSIS", Set.of(
                    "drive", "suspension", "frontBrakes", "backBrakes",
                    "frontBrakesRadius", "backBrakesRadius"),

            "PERFORMANCE", Set.of(
                    "maxSpeed", "acceleration0100", "fuelConsumptionCity", "fuelConsumptionRoute",
                    "fuelConsumptionMixed", "fuelTankCapacity"),

            "OUTSIDE_DIMENSIONS", Set.of(
                    "length", "width", "height", "wheelBase", "clearance"),

            "INSIDE_DIMENSIONS", Set.of(
                    "minTrunkSpace", "maxTrunkSpace", "heightFromSeatToRoofFront", "heightFromSeatToRoofBack"),

            "BASIC_INFO", Set.of(
                    "doorsNumber", "seatsNumber", "productionYears", "description")
    );

    private DataProposalFields() {
    }


    // Accept both "engine" and "ENGINE"
    public static String normalizeCategory(String category) {
        return category == null ? "" : category.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean isKnownCategory(String category) {
        return ALLOWED_FIELDS.containsKey(normalizeCategory(category));
    }

    public static Set<String> allowedFieldsFor(String category) {
        return ALLOWED_FIELDS.getOrDefault(normalizeCategory(category), Set.of());
    }


    /**
     * Removes every entry that is not an editable field of the given category.
     */
    public static Map<String, Object> filter(String category, Map<String, Object> proposedValues) {
        String normalizedCategory = normalizeCategory(category);

        if (!ALLOWED_FIELDS.containsKey(normalizedCategory))
            throw new IllegalArgumentException("Unknown category: " + category + ".");

        Set<String> allowedFields = ALLOWED_FIELDS.get(normalizedCategory);
        Map<String, Object> filteredValues = new LinkedHashMap<>();

        if (proposedValues != null)
            proposedValues.forEach((field, value) -> {
                if (allowedFields.contains(field))
                    filteredValues.put(field, value);
            });

        return filteredValues;
    }
}
