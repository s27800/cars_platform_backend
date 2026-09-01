package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.common.ProposalCategory;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * Fields a user is allowed to propose a change for (for each category).
 */
public final class DataProposalFields {

    private static final Map<ProposalCategory, Set<String>> ALLOWED_FIELDS = new EnumMap<>(ProposalCategory.class);

    static {
        ALLOWED_FIELDS.put(ProposalCategory.ENGINE, Set.of(
                "engineCode", "displacement", "engineType", "maxPower", "maxPowerRotationSpeed",
                "maxTorque", "maxTorqueRotationSpeed", "cylindersNumber", "valvesNumber", "turbo"));

        ALLOWED_FIELDS.put(ProposalCategory.TRANSMISSION, Set.of(
                "transmissionType", "transmissionName", "gearsNumber", "clutchType"));

        ALLOWED_FIELDS.put(ProposalCategory.CHASSIS, Set.of(
                "drive", "suspension", "frontBrakes", "backBrakes",
                "frontBrakesRadius", "backBrakesRadius"));

        ALLOWED_FIELDS.put(ProposalCategory.PERFORMANCE, Set.of(
                "maxSpeed", "acceleration0100", "fuelConsumptionCity", "fuelConsumptionRoute",
                "fuelConsumptionMixed", "fuelTankCapacity"));

        ALLOWED_FIELDS.put(ProposalCategory.OUTSIDE_DIMENSIONS, Set.of(
                "length", "width", "height", "wheelBase", "clearance"));

        ALLOWED_FIELDS.put(ProposalCategory.INSIDE_DIMENSIONS, Set.of(
                "minTrunkSpace", "maxTrunkSpace", "heightFromSeatToRoofFront", "heightFromSeatToRoofBack"));

        ALLOWED_FIELDS.put(ProposalCategory.BASIC_INFO, Set.of(
                "doorsNumber", "seatsNumber", "productionYears", "description"));

        ALLOWED_FIELDS.put(ProposalCategory.TAGS, Set.of(
                "addTagIds", "removeTagIds"));
    }


    private DataProposalFields() {
    }


    public static Set<String> allowedFieldsFor(ProposalCategory category) {
        return ALLOWED_FIELDS.getOrDefault(category, Set.of());
    }

    /**
     * Removes every entry that is not an editable field of the given category.
     */
    public static Map<String, Object> filter(
            ProposalCategory category,
            Map<String, Object> proposedValues
    ) {
        Set<String> allowedFields = ALLOWED_FIELDS.get(category);

        if (allowedFields == null)
            throw new IllegalArgumentException("Unknown category: " + category + ".");

        Map<String, Object> filteredValues = new LinkedHashMap<>();

        if (proposedValues != null)
            proposedValues.forEach((field, value) -> {
                if (allowedFields.contains(field))
                    filteredValues.put(field, value);
            });

        return filteredValues;
    }
}
