package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;


@DisplayName("GetDataProposalsMapper Tests")
class GetDataProposalsMapperTest {

    private final GetDataProposalsMapper mapper = Mappers.getMapper(GetDataProposalsMapper.class);

    private DataProposal testProposal;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(1)
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(1)
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(1)
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .name("BMW 320i")
                .images(List.of())
                .tags(new HashSet<>())
                .build();

        // Create test user
        User user = TestDataFactory.defaultUser()
                .id(1L)
                .build();

        // Create test proposal
        testProposal = new DataProposal();

        testProposal.setId(1L);
        testProposal.setUser(user);
        testProposal.setCar(testCar);
        testProposal.setCategory("engine");
        testProposal.setComment("Proposed change");
        testProposal.setProposedValues(Map.of("maxPower", 200));
        testProposal.setStatus(DataProposalStatus.PENDING);
        testProposal.setAdminComment(null);
        testProposal.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void nullInput_ReturnsNull() {

            // Map null input
            GetDataProposalsResponse result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void validInput_MapsAllFields() {

            // Map valid input
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(1);
            assertThat(result.getCarInfo().getCarName()).isEqualTo("BMW 320i");
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("Test Brand");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("Test Model");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("Gen I");
            assertThat(result.getCategory()).isEqualTo("engine");
            assertThat(result.getComment()).isEqualTo("Proposed change");
            assertThat(result.getProposedValues()).containsEntry("maxPower", 200);
            assertThat(result.getStatus()).isEqualTo(DataProposalStatus.PENDING);
            assertThat(result.getAdminComment()).isNull();
            assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(result.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("should map carInfo from car entity")
        void validInput_MapsCarInfo() {

            // Map valid input
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            // Verify carInfo is mapped correctly
            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(1);
            assertThat(result.getCarInfo().getCarName()).isEqualTo("BMW 320i");
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("Test Brand");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("Test Model");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("Gen I");
        }

        @Test
        @DisplayName("should map approved status and admin comment")
        void approvedStatus_MapsCorrectly() {

            // Set proposal fields
            testProposal.setStatus(DataProposalStatus.APPROVED);
            testProposal.setAdminComment("Approved by admin");
            testProposal.setResolvedAt(LocalDateTime.of(2024, 1, 16, 14, 0));

            // Map valid input
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            // Verify status and admin comment are mapped correctly
            assertThat(result.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(result.getAdminComment()).isEqualTo("Approved by admin");
            assertThat(result.getResolvedAt()).isEqualTo(LocalDateTime.of(2024, 1, 16, 14, 0));
        }

        @Test
        @DisplayName("should map rejected status and admin comment")
        void rejectedStatus_MapsCorrectly() {

            // Set proposal fields
            testProposal.setStatus(DataProposalStatus.REJECTED);
            testProposal.setAdminComment("Invalid data");
            testProposal.setResolvedAt(LocalDateTime.of(2024, 1, 16, 15, 30));

            // Map valid input
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            // Verify results -> status and admin comment are mapped correctly
            assertThat(result.getStatus()).isEqualTo(DataProposalStatus.REJECTED);
            assertThat(result.getAdminComment()).isEqualTo("Invalid data");
            assertThat(result.getResolvedAt()).isEqualTo(LocalDateTime.of(2024, 1, 16, 15, 30));
        }
    }
}
