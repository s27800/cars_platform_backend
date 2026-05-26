package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.users.User;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AdminReviewService Tests")
class AdminReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private AdminReviewMapper adminReviewMapper;

    @InjectMocks
    private AdminReviewService adminReviewService;

    private User testUser;
    private Car testCar;
    private Review testReview;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();

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
                .build();

        // Create test review
        testReview = TestDataFactory.defaultReview(testUser, testCar)
                .id(1L)
                .isApproved(false)
                .build();
    }


    @Nested
    @DisplayName("getPendingReviews")
    class GetPendingReviewsTests {

        @Test
        @DisplayName("should return pending reviews page")
        void getPendingReviews_ReturnsPendingReviews() {

            // Create pageable and mock data
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> reviewPage = new PageImpl<>(List.of(testReview));
            Page<AdminReviewResponse> expectedResponse = new PageImpl<>(List.of(mock(AdminReviewResponse.class)));

            // Mock repository and mapper
            when(reviewRepository.findAllPending(pageable)).thenReturn(reviewPage);
            when(adminReviewMapper.toDtoList(reviewPage)).thenReturn(expectedResponse);

            // Get pending reviews
            Page<AdminReviewResponse> result = adminReviewService.getPendingReviews(pageable);

            // Verify results -> pending reviews page is returned with correct content
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(reviewRepository).findAllPending(pageable);
            verify(adminReviewMapper).toDtoList(reviewPage);
        }

        @Test
        @DisplayName("should return empty page when no pending reviews")
        void getPendingReviews_NoPendingReviews_ReturnsEmptyPage() {

            // Create pageable and mock data
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> emptyPage = Page.empty();
            Page<AdminReviewResponse> emptyResponse = Page.empty();

            // Mock repository and mapper
            when(reviewRepository.findAllPending(pageable)).thenReturn(emptyPage);
            when(adminReviewMapper.toDtoList(emptyPage)).thenReturn(emptyResponse);

            // Get pending reviews
            Page<AdminReviewResponse> result = adminReviewService.getPendingReviews(pageable);

            // Verify results -> empty page is returned
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

            // Mock repository
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Approve review
            adminReviewService.approveReview(1L, true);

            // Verify results -> review is approved
            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

            verify(reviewRepository).save(reviewCaptor.capture());

            Review savedReview = reviewCaptor.getValue();

            assertThat(savedReview.getIsApproved()).isTrue();
        }

        @Test
        @DisplayName("should reject review when approve is false")
        void approveReview_ApproveFalse_RejectsReview() {

            // Set review as approved
            testReview.setIsApproved(true);

            // Mock repository
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Reject review
            adminReviewService.approveReview(1L, false);

            // Verify results -> review is rejected
            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

            verify(reviewRepository).save(reviewCaptor.capture());

            Review savedReview = reviewCaptor.getValue();

            assertThat(savedReview.getIsApproved()).isFalse();
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when review not found")
        void approveReview_ReviewNotFound_ThrowsException() {

            // Mock repository
            when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

            // Approve review and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> adminReviewService.approveReview(999L, true))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewRepository, never()).save(any());
        }
    }
}
