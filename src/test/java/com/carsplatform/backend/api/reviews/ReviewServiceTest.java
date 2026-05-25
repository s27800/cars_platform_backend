package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;
import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
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
@DisplayName("ReviewService Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private CreateReviewMapper createReviewMapper;

    @InjectMocks
    private ReviewService reviewService;

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
                .build();
    }


    @Nested
    @DisplayName("getAverageRatingsForCar")
    class GetAverageRatingsForCarTests {

        @Test
        @DisplayName("should return average ratings when car exists")
        void getAverageRatingsForCar_CarExists_ReturnsAverageRatings() {

            // Create expected average ratings response
            AverageRatingsResponse expectedResponse = mock(AverageRatingsResponse.class);

            // Mock repository and service calls
            when(carRepository.existsById(1)).thenReturn(true);
            when(reviewRepository.findAverageRatingsForCarId(1)).thenReturn(expectedResponse);

            // Get average ratings for car
            AverageRatingsResponse result = reviewService.getAverageRatingsForCar(1);

            // Verify results -> correct response returned
            assertThat(result).isNotNull();

            verify(carRepository).existsById(1);
            verify(reviewRepository).findAverageRatingsForCarId(1);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when car does not exist")
        void getAverageRatingsForCar_CarNotFound_ThrowsException() {

            // Mock repository
            when(carRepository.existsById(999)).thenReturn(false);

            // Get average ratings for non-existent car and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> reviewService.getAverageRatingsForCar(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewRepository, never()).findAverageRatingsForCarId(anyInt());
        }
    }


    @Nested
    @DisplayName("getReviewsForCarId")
    class GetReviewsForCarIdTests {

        @Test
        @DisplayName("should return paginated reviews for car")
        void getReviewsForCarId_CarExists_ReturnsReviews() {

            // Create page with specific pagination
            Pageable pageable = PageRequest.of(0, 10);
            Page<Review> reviewPage = new PageImpl<>(List.of(testReview), pageable, 1);
            Page<ReviewResponse> expectedResponse = new PageImpl<>(
                    List.of(ReviewResponse.builder().id(1L).build()),
                    pageable, 1
            );

            // Mock repository
            when(reviewRepository.findAllApprovedByCarId(1, pageable)).thenReturn(reviewPage);
            when(reviewMapper.toDtoList(reviewPage)).thenReturn(expectedResponse);

            // Get paginated reviews for car
            Page<ReviewResponse> result = reviewService.getReviewsForCarId(1, pageable);

            // Verify results -> correct response returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(reviewRepository).findAllApprovedByCarId(1, pageable);
            verify(reviewMapper).toDtoList(reviewPage);
        }
    }


    @Nested
    @DisplayName("createReview")
    class CreateReviewTests {

        @Test
        @DisplayName("should create review when user and car exist and no previous review exists")
        void createReview_ValidData_CreatesReview() {

            // Create valid request
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Great car, highly recommended!")
                    .engineRating(5.0)
                    .transmissionRating(4.0)
                    .steeringRating(4.0)
                    .suspensionRating(4.0)
                    .visibilityRating(5.0)
                    .ergonomicsRating(4.0)
                    .soundProofingRating(3.0)
                    .interiorSpaceRating(4.0)
                    .maintenanceRating(3.0)
                    .priceQualityRating(4.0)
                    .build();

            Review mappedReview = new Review();

            // Mock repository
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(carRepository.findById(1)).thenReturn(Optional.of(testCar));
            when(reviewRepository.existsByCarIdAndUserId(1, 1L)).thenReturn(false);
            when(createReviewMapper.toDto(request)).thenReturn(mappedReview);

            // Create review
            reviewService.createReview(1, request, "testuser");

            // Verify results -> correct review created
            ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);

            verify(reviewRepository).save(reviewCaptor.capture());

            Review savedReview = reviewCaptor.getValue();

            assertThat(savedReview.getCar()).isEqualTo(testCar);
            assertThat(savedReview.getUser()).isEqualTo(testUser);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void createReview_UserNotFound_ThrowsException() {

            // Create request
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Great car!")
                    .build();

            // Mock repository
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Create review with non-existent user and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> reviewService.createReview(1, request, "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when car not found")
        void createReview_CarNotFound_ThrowsException() {

            // Create request
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Great car!")
                    .build();

            // Mock repository
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(carRepository.findById(999)).thenReturn(Optional.empty());

            // Create review with non-existent car and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> reviewService.createReview(999, request, "testuser"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(reviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceAlreadyExistsException when user already reviewed car")
        void createReview_AlreadyReviewed_ThrowsException() {

            // Create request
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Another review!")
                    .build();

            // Mock repository
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(carRepository.findById(1)).thenReturn(Optional.of(testCar));
            when(reviewRepository.existsByCarIdAndUserId(1, 1L)).thenReturn(true);

            // Create review with already reviewed car and verify result -> ResourceAlreadyExistsException is thrown
            assertThatThrownBy(() -> reviewService.createReview(1, request, "testuser"))
                    .isInstanceOf(ResourceAlreadyExistsException.class);

            verify(reviewRepository, never()).save(any());
        }
    }
}
