package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewDetailsMapper;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.reviews.dtos.ReviewDetailsResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AdminReviewService Tests")
class AdminReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewDetailsMapper reviewDetailsMapper;

    @InjectMocks
    private AdminReviewService adminReviewService;

    private User testUser;
    private Car testCar;
    private Review testReview;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
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
                .build();
        testReview = TestDataFactory.defaultReview(testUser, testCar)
                .id(UUID.randomUUID())
                .status(ModerationStatus.PENDING)
                .build();
    }


    @Nested
    @DisplayName("getPendingReviews")
    class GetPendingReviewsTests {

        @Test
        @DisplayName("should return pending reviews page")
        void getPendingReviews_ReturnsPendingReviews() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
            Page<ReviewDetailsResponse> expectedResponse = new PageImpl<>(List.of(mock(ReviewDetailsResponse.class)));

            when(reviewRepository.findAllPending(pageable)).thenReturn(reviewPage);
            when(reviewDetailsMapper.toDtoList(reviewPage)).thenReturn(expectedResponse);

            Page<ReviewDetailsResponse> result = adminReviewService.getPendingReviews(pageable);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(reviewRepository).findAllPending(pageable);
            verify(reviewDetailsMapper).toDtoList(reviewPage);
        }

        @Test
        @DisplayName("should return empty page when no pending reviews")
        void getPendingReviews_NoPendingReviews_ReturnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> emptyPage = Page.empty();
            Page<ReviewDetailsResponse> emptyResponse = Page.empty();

            when(reviewRepository.findAllPending(pageable)).thenReturn(emptyPage);
            when(reviewDetailsMapper.toDtoList(emptyPage)).thenReturn(emptyResponse);

            Page<ReviewDetailsResponse> result = adminReviewService.getPendingReviews(pageable);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();

            verify(reviewRepository).findAllPending(pageable);
        }
    }


    @Nested
    @DisplayName("approveReview")
    class ApproveReviewTests {

        @Test
        @DisplayName("should approve review when review exists")
        void approveReview_ReviewExists_ApprovesReview() {
            when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            adminReviewService.approveReview(testReview.getId(), true);
            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

            verify(reviewRepository).save(reviewCaptor.capture());

            Review savedReview = reviewCaptor.getValue();

            assertThat(savedReview.getStatus()).isEqualTo(ModerationStatus.APPROVED);
        }

        @Test
        @DisplayName("should set status to REJECTED when approve is false")
        void approveReview_ApproveFalse_SetsStatusRejected() {
            when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));

            adminReviewService.approveReview(testReview.getId(), false);
            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
            verify(reviewRepository).save(reviewCaptor.capture());
            verify(reviewRepository, never()).delete(any());

            Review savedReview = reviewCaptor.getValue();
            assertThat(savedReview.getStatus()).isEqualTo(ModerationStatus.REJECTED);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when review not found")
        void approveReview_ReviewNotFound_ThrowsException() {
            UUID nonExistentId = UUID.randomUUID();
            when(reviewRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminReviewService.approveReview(nonExistentId, true))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewRepository, never()).save(any());
        }
    }
}
