package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;


@DisplayName("CreateReviewMapper Tests")
class CreateReviewMapperTest {

    private final CreateReviewMapper mapper = Mappers.getMapper(CreateReviewMapper.class);

    @Nested
    @DisplayName("toDto")
    class ToDtoTests {

        @Test
        @DisplayName("should return null when input is null")
        void nullInput_ReturnsNull() {

            // Map null input
            Review result = mapper.toDto(null);

            // Verify result is null
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all rating fields from request")
        void validRequest_MapsAllRatings() {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Great car!")
                    .engineRating(5.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(3.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(5.0)
                    .soundProofingRating(3.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(3.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();

            // Map valid input
            Review result = mapper.toDto(request);

            // Verify all fields are mapped correctly
            assertThat(result).isNotNull();
            assertThat(result.getComment()).isEqualTo("Great car!");
            assertThat(result.getEngineRating()).isEqualTo(5);
            assertThat(result.getTransmissionRating()).isEqualTo(4);
            assertThat(result.getSteeringRating()).isEqualTo(4);
            assertThat(result.getSuspensionRating()).isEqualTo(3);
            assertThat(result.getVisibilityRating()).isEqualTo(4);
            assertThat(result.getErgonomicsRating()).isEqualTo(5);
            assertThat(result.getSoundProofingRating()).isEqualTo(3);
            assertThat(result.getInteriorSpaceRating()).isEqualTo(4);
            assertThat(result.getMaintenanceRating()).isEqualTo(3);
            assertThat(result.getPriceQualityRating()).isEqualTo(4);
            assertThat(result.getFailureFreeRating()).isEqualTo(4);
        }

        @Test
        @DisplayName("should set isApproved to false")
        void validRequest_SetsIsApprovedFalse() {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(4.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(4.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(4.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();

            // Map valid input
            Review result = mapper.toDto(request);

            // Verify isApproved is set to false
            assertThat(result.getIsApproved()).isFalse();
        }

        @Test
        @DisplayName("should set reviewDate to current time")
        void validRequest_SetsReviewDate() {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(4.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(4.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(4.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();

            // Map valid input
            Review result = mapper.toDto(request);

            // Verify reviewDate is set to current time
            assertThat(result.getReviewDate()).isNotNull();
        }

        @Test
        @DisplayName("should ignore id, user and car fields")
        void validRequest_IgnoresIdUserCar() {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(4.0)
                    .visibilityRating(4.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(4.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(4.0)
                    .priceQualityRating(4.0)
                    .failureFreeRating(4.0)
                    .build();

            // Map valid input
            Review result = mapper.toDto(request);

            // Verify id, user, and car fields are ignored
            assertThat(result.getId()).isNull();
            assertThat(result.getUser()).isNull();
            assertThat(result.getCar()).isNull();
        }
    }
}
