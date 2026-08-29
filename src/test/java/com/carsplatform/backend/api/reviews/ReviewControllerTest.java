package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("ReviewController Integration Tests")
class ReviewControllerTest extends MockMvcTestBase {

    private static final String REVIEW_BASE_URL = "/api/reviews";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Car testCar;
    private User testUser;
    private Review testReview;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .name("3 Series")
                .build();

        entityManager.persist(model);

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("E90")
                .build();

        entityManager.persist(generation);

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .name("320i")
                .build();

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        // Register user and get token
        String username = "reviewuser" + System.currentTimeMillis();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("Review")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();

        // Create approved test review
        testReview = Review.builder()
                .user(testUser)
                .car(testCar)
                .comment("Great car for daily commute")
                .engineRating(5)
                .transmissionRating(4)
                .steeringRating(4)
                .suspensionRating(4)
                .visibilityRating(4)
                .ergonomicsRating(4)
                .soundProofingRating(3)
                .interiorSpaceRating(4)
                .maintenanceRating(4)
                .priceQualityRating(4)
                .failureFreeRating(4)
                .isApproved(true)
                .reviewDate(LocalDateTime.now())
                .build();

        entityManager.persist(testReview);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/reviews/{carId}")
    class GetReviewsTests {

        @Test
        @DisplayName("returns reviews for car (public endpoint)")
        void getReviews_ExistingCar_Returns200() throws Exception {

            // Perform GET request and verify response -> returns 200 OK with reviews for the car
            performGetNoAuth(REVIEW_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns reviews with user info")
        void getReviews_ExistingCar_IncludesUserInfo() throws Exception {

            // Perform GET request and verify response -> reviews include user info
            performGetNoAuth(REVIEW_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].usernameResponse.username").exists());
        }

        @Test
        @DisplayName("returns paginated results")
        void getReviews_WithPagination_ReturnsPaginated() throws Exception {

            // Perform GET request and verify response -> returns paginated results
            performGetNoAuth(REVIEW_BASE_URL + "/" + testCar.getId() + "?page=0&size=5")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }

        @Test
        @DisplayName("returns empty for car with no reviews")
        void getReviews_NoReviews_ReturnsEmpty() throws Exception {

            // Create a new car with no reviews
            Brand brand = TestDataFactory.createBrand("Audi");
            entityManager.persist(brand);

            Model model = TestDataFactory.createModel(brand, "A4");
            entityManager.persist(model);

            Generation gen = TestDataFactory.createGeneration(model, "B8");
            entityManager.persist(gen);

            BodyType bt = TestDataFactory.createBodyType("Hatchback");
            entityManager.persist(bt);

            Car carWithoutReviews = TestDataFactory.defaultCar(gen, bt).name("A4 2.0").build();

            entityManager.persist(carWithoutReviews.getEngine());
            entityManager.persist(carWithoutReviews.getTransmission());
            entityManager.persist(carWithoutReviews.getChassis());
            entityManager.persist(carWithoutReviews.getPerformance());
            entityManager.persist(carWithoutReviews.getInsideDimensions());
            entityManager.persist(carWithoutReviews.getOutsideDimensions());
            entityManager.persist(carWithoutReviews);

            entityManager.flush();

            // Perform GET request and verify response -> returns empty for car with no reviews
            performGetNoAuth(REVIEW_BASE_URL + "/" + carWithoutReviews.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }


    @Nested
    @DisplayName("GET /api/reviews/{carId}/average-ratings")
    class GetAverageRatingsTests {

        @Test
        @DisplayName("returns average ratings for car (public endpoint)")
        void getAverageRatings_ExistingCar_Returns200() throws Exception {

            // Perform GET request and verify response -> returns average ratings for car
            performGetNoAuth(REVIEW_BASE_URL + "/" + testCar.getId() + "/average-ratings")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.avgEngineRating").isNumber())
                    .andExpect(jsonPath("$.avgTransmissionRating").isNumber());
        }
    }


    @Nested
    @DisplayName("POST /api/reviews/{carId}")
    class CreateReviewTests {

        @Test
        @DisplayName("creates review when authenticated")
        void createReview_Authenticated_Returns201() throws Exception {

            // Create a new user and get token for authentication
            String newUsername = "newreviewer" + System.currentTimeMillis();

            RegisterRequest newUserRequest = RegisterRequest.builder()
                    .username(newUsername)
                    .email(newUsername + "@example.com")
                    .password("Password123!")
                    .firstName("New")
                    .lastName("Reviewer")
                    .build();

            String response = performPostNoAuth(AUTH_BASE_URL + "/register", newUserRequest)
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String newUserToken = objectMapper.readTree(response).get("accessToken").asText();

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("This is a great test car review comment")
                    .engineRating(5.0)
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

            // Perform POST request and verify response -> returns 201 Created
            performPostWithAuth(REVIEW_BASE_URL + "/" + testCar.getId(), request, newUserToken)
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void createReview_NotAuthenticated_Returns401() throws Exception {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("This review should not be created")
                    .engineRating(5.0)
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

            // Perform POST request and verify response -> returns 401 Unauthorized
            performPostNoAuth(REVIEW_BASE_URL + "/" + testCar.getId(), request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 400 when comment is too short")
        void createReview_CommentTooShort_Returns400() throws Exception {

            // Create valid request with all rating fields set
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("Short")
                    .engineRating(5.0)
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

            // Perform POST request and verify response -> returns 400 Bad Request
            performPostWithAuth(REVIEW_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when ratings are missing")
        void createReview_MissingRatings_Returns400() throws Exception {

            // Create valid request with missing rating fields
            CreateReviewRequest request = CreateReviewRequest.builder()
                    .comment("This is a valid length comment for testing purposes")
                    .engineRating(5.0)
                    .build();

            // Perform POST request and verify response -> returns 400 Bad Request
            performPostWithAuth(REVIEW_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }
}
