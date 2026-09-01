package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.common.ModerationStatus;

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
            Review result = mapper.toEntity(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should map all rating fields from request")
        void validRequest_MapsAllRatings() {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Great car!")
                    .engineRating(5)
                    .transmissionRating(4)
                    .steeringRating(4)
                    .suspensionRating(3)
                    .visibilityRating(4)
                    .ergonomicsRating(5)
                    .soundProofingRating(3)
                    .interiorSpaceRating(4)
                    .maintenanceRating(3)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();

            Review result = mapper.toEntity(request);

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
        @DisplayName("should set status to PENDING")
        void validRequest_SetsStatusPending() {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4)
                    .transmissionRating(4)
                    .steeringRating(4)
                    .suspensionRating(4)
                    .visibilityRating(4)
                    .ergonomicsRating(4)
                    .soundProofingRating(4)
                    .interiorSpaceRating(4)
                    .maintenanceRating(4)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();

            Review result = mapper.toEntity(request);

            assertThat(result.getStatus()).isEqualTo(ModerationStatus.PENDING);
        }

        @Test
        @DisplayName("should set reviewDate to current time")
        void validRequest_SetsReviewDate() {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4)
                    .transmissionRating(4)
                    .steeringRating(4)
                    .suspensionRating(4)
                    .visibilityRating(4)
                    .ergonomicsRating(4)
                    .soundProofingRating(4)
                    .interiorSpaceRating(4)
                    .maintenanceRating(4)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();

            Review result = mapper.toEntity(request);

            assertThat(result.getReviewDate()).isNotNull();
        }

        @Test
        @DisplayName("should ignore id, user and car fields")
        void validRequest_IgnoresIdUserCar() {
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Test comment")
                    .engineRating(4)
                    .transmissionRating(4)
                    .steeringRating(4)
                    .suspensionRating(4)
                    .visibilityRating(4)
                    .ergonomicsRating(4)
                    .soundProofingRating(4)
                    .interiorSpaceRating(4)
                    .maintenanceRating(4)
                    .priceQualityRating(4)
                    .failureFreeRating(4)
                    .build();

            Review result = mapper.toEntity(request);

            assertThat(result.getId()).isNull();
            assertThat(result.getUser()).isNull();
            assertThat(result.getCar()).isNull();
        }
    }
}
