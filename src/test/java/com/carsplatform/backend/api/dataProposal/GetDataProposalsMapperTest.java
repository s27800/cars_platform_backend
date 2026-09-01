package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.ProposalCategory;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@DisplayName("GetDataProposalsMapper Tests")
class GetDataProposalsMapperTest {

    @Autowired
    private GetDataProposalsMapper mapper;

    private DataProposal testProposal;
    private Car testCar;

    @BeforeEach
    void setUp() {
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .name("BMW 320i")
                .images(new LinkedHashSet<>())
                .tags(new HashSet<>())
                .build();
        User user = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        testProposal = new DataProposal();

        testProposal.setId(UUID.randomUUID());
        testProposal.setUser(user);
        testProposal.setCar(testCar);
        testProposal.setCategory(ProposalCategory.ENGINE);
        testProposal.setComment("Proposed change");
        testProposal.setProposedValues(Map.of("maxPower", 200));
        testProposal.setStatus(ModerationStatus.PENDING);
        testProposal.setAdminComment(null);
        testProposal.setCreatedAt(LocalDateTime.of(2024, 1, 15, 10, 30));
    }


    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void nullInput_ReturnsNull() {
            GetDataProposalsResponse result = mapper.toDto(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all fields correctly")
        void validInput_MapsAllFields() {
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testProposal.getId());
            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarInfo().getCarName()).isEqualTo("BMW 320i");
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("Test Brand");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("Test Model");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("Gen I");
            assertThat(result.getCategory()).isEqualTo(ProposalCategory.ENGINE);
            assertThat(result.getComment()).isEqualTo("Proposed change");
            assertThat(result.getProposedValues()).containsEntry("maxPower", 200);
            assertThat(result.getStatus()).isEqualTo(ModerationStatus.PENDING);
            assertThat(result.getAdminComment()).isNull();
            assertThat(result.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 10, 30));
            assertThat(result.getResolvedAt()).isNull();
        }

        @Test
        @DisplayName("should map carInfo from car entity")
        void validInput_MapsCarInfo() {
            GetDataProposalsResponse result = mapper.toDto(testProposal);

            assertThat(result.getCarInfo()).isNotNull();
            assertThat(result.getCarInfo().getCarId()).isEqualTo(testCar.getId());
            assertThat(result.getCarInfo().getCarName()).isEqualTo("BMW 320i");
            assertThat(result.getCarInfo().getBrandName()).isEqualTo("Test Brand");
            assertThat(result.getCarInfo().getModelName()).isEqualTo("Test Model");
            assertThat(result.getCarInfo().getGenerationName()).isEqualTo("Gen I");
        }

        @Test
        @DisplayName("should map approved status and admin comment")
        void approvedStatus_MapsCorrectly() {
            testProposal.setStatus(ModerationStatus.APPROVED);
            testProposal.setAdminComment("Approved by admin");
            testProposal.setResolvedAt(LocalDateTime.of(2024, 1, 16, 14, 0));

            GetDataProposalsResponse result = mapper.toDto(testProposal);

            assertThat(result.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(result.getAdminComment()).isEqualTo("Approved by admin");
            assertThat(result.getResolvedAt()).isEqualTo(LocalDateTime.of(2024, 1, 16, 14, 0));
        }

        @Test
        @DisplayName("should map rejected status and admin comment")
        void rejectedStatus_MapsCorrectly() {
            testProposal.setStatus(ModerationStatus.REJECTED);
            testProposal.setAdminComment("Invalid data");
            testProposal.setResolvedAt(LocalDateTime.of(2024, 1, 16, 15, 30));

            GetDataProposalsResponse result = mapper.toDto(testProposal);
            assertThat(result.getStatus()).isEqualTo(ModerationStatus.REJECTED);
            assertThat(result.getAdminComment()).isEqualTo("Invalid data");
            assertThat(result.getResolvedAt()).isEqualTo(LocalDateTime.of(2024, 1, 16, 15, 30));
        }
    }
}
