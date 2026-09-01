package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.common.ProposalCategory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;


@DisplayName("DataProposalFields Tests")
class DataProposalFieldsTest {

    @Nested
    @DisplayName("allowedFieldsFor")
    class AllowedFieldsForTests {

        @ParameterizedTest
        @EnumSource(ProposalCategory.class)
        @DisplayName("Should list at least one editable field for every category")
        void shouldListFieldsForEveryCategory(ProposalCategory category) {
            assertThat(DataProposalFields.allowedFieldsFor(category)).isNotEmpty();
        }

        @Test
        @DisplayName("Should list the engine fields for the ENGINE category")
        void shouldListEngineFields() {
            Set<String> fields = DataProposalFields.allowedFieldsFor(ProposalCategory.ENGINE);

            assertThat(fields).contains("engineCode", "displacement", "maxPower", "turbo");
        }

        @Test
        @DisplayName("Should list only the tag fields for the TAGS category")
        void shouldListTagFields() {
            Set<String> fields = DataProposalFields.allowedFieldsFor(ProposalCategory.TAGS);

            assertThat(fields).containsExactlyInAnyOrder("addTagIds", "removeTagIds");
        }

        @Test
        @DisplayName("Should return an empty set for a null category")
        void shouldReturnEmptySetForNullCategory() {
            assertThat(DataProposalFields.allowedFieldsFor(null)).isEmpty();
        }

        @Test
        @DisplayName("Should keep the categories disjoint so a field belongs to one category only")
        void shouldKeepCategoriesDisjoint() {
            for (ProposalCategory first : ProposalCategory.values())
                for (ProposalCategory second : ProposalCategory.values()) {
                    if (first == second)
                        continue;

                    assertThat(DataProposalFields.allowedFieldsFor(first))
                            .as("%s and %s share a field", first, second)
                            .doesNotContainAnyElementsOf(DataProposalFields.allowedFieldsFor(second));
                }
        }

        @Test
        @DisplayName("Should not expose the returned set to modification")
        void shouldReturnAnImmutableSet() {
            Set<String> fields = DataProposalFields.allowedFieldsFor(ProposalCategory.ENGINE);

            assertThatThrownBy(() -> fields.add("horsepower"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }


    @Nested
    @DisplayName("filter")
    class FilterTests {

        @Test
        @DisplayName("Should keep the values that belong to the category")
        void shouldKeepAllowedValues() {
            Map<String, Object> proposed = Map.of(
                    "engineCode", "N20B20",
                    "displacement", 1997,
                    "turbo", true);

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered).containsAllEntriesOf(proposed);
        }

        @Test
        @DisplayName("Should drop the values that do not belong to the category")
        void shouldDropUnknownValues() {
            Map<String, Object> proposed = new LinkedHashMap<>();
            proposed.put("engineCode", "N20B20");
            proposed.put("maxSpeed", 250);
            proposed.put("nonExistentField", "x");

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered).containsOnlyKeys("engineCode");
        }

        @Test
        @DisplayName("Should drop a field that is editable in a different category")
        void shouldDropFieldOfAnotherCategory() {
            Map<String, Object> proposed = Map.of("gearsNumber", 8);

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered).isEmpty();
        }

        @Test
        @DisplayName("Should reject an attempt to change identifiers or ownership")
        void shouldRejectSensitiveFields() {
            Map<String, Object> proposed = new LinkedHashMap<>();
            proposed.put("id", "00000000-0000-0000-0000-000000000000");
            proposed.put("carId", "11111111-1111-1111-1111-111111111111");
            proposed.put("user", "admin");
            proposed.put("status", "APPROVED");
            proposed.put("maxPower", 184);

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered).containsOnlyKeys("maxPower");
        }

        @Test
        @DisplayName("Should keep a null value of an allowed field")
        void shouldKeepNullValueOfAllowedField() {
            Map<String, Object> proposed = new LinkedHashMap<>();
            proposed.put("engineCode", null);

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered).containsKey("engineCode");
            assertThat(filtered.get("engineCode")).isNull();
        }

        @Test
        @DisplayName("Should preserve the order the values were proposed in")
        void shouldPreserveInsertionOrder() {
            Map<String, Object> proposed = new LinkedHashMap<>();
            proposed.put("turbo", true);
            proposed.put("engineCode", "N20B20");
            proposed.put("displacement", 1997);

            Map<String, Object> filtered = DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(filtered.keySet()).containsExactly("turbo", "engineCode", "displacement");
        }

        @Test
        @DisplayName("Should return an empty map for null proposed values")
        void shouldReturnEmptyMapForNullValues() {
            assertThat(DataProposalFields.filter(ProposalCategory.ENGINE, null)).isEmpty();
        }

        @Test
        @DisplayName("Should return an empty map for empty proposed values")
        void shouldReturnEmptyMapForEmptyValues() {
            assertThat(DataProposalFields.filter(ProposalCategory.ENGINE, Map.of())).isEmpty();
        }

        @Test
        @DisplayName("Should reject a null category")
        void shouldRejectNullCategory() {
            Map<String, Object> proposed = Map.of("engineCode", "N20B20");

            assertThatThrownBy(() -> DataProposalFields.filter(null, proposed))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Unknown category");
        }

        @Test
        @DisplayName("Should not modify the map it was given")
        void shouldNotModifyTheInputMap() {
            Map<String, Object> proposed = new LinkedHashMap<>();
            proposed.put("engineCode", "N20B20");
            proposed.put("maxSpeed", 250);

            DataProposalFields.filter(ProposalCategory.ENGINE, proposed);

            assertThat(proposed).containsOnlyKeys("engineCode", "maxSpeed");
        }

        @ParameterizedTest
        @EnumSource(ProposalCategory.class)
        @DisplayName("Should accept every field it declares as editable, for every category")
        void shouldAcceptEveryDeclaredField(ProposalCategory category) {
            Map<String, Object> proposed = new LinkedHashMap<>();
            DataProposalFields.allowedFieldsFor(category).forEach(field -> proposed.put(field, "value"));

            Map<String, Object> filtered = DataProposalFields.filter(category, proposed);

            assertThat(filtered.keySet()).isEqualTo(DataProposalFields.allowedFieldsFor(category));
        }
    }
}
