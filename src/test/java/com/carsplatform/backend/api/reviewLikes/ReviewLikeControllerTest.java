package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.ModerationStatus;
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


@DisplayName("ReviewLikeController Integration Tests")
class ReviewLikeControllerTest extends MockMvcTestBase {

    private static final String LIKE_BASE_URL = "/api/likes/review";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Review testReview;
    private User testUser;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        Brand brand = TestDataFactory.defaultBrand().name("BMW").build();
        entityManager.persist(brand);

        Model model = TestDataFactory.defaultModel(brand).name("3 Series").build();
        entityManager.persist(model);

        Generation generation = TestDataFactory.defaultGeneration(model).name("E90").build();
        entityManager.persist(generation);

        BodyType bodyType = TestDataFactory.defaultBodyType().name("Sedan").build();
        entityManager.persist(bodyType);

        Car car = TestDataFactory.defaultCar(generation, bodyType).name("320i").build();
        entityManager.persist(car.getEngine());
        entityManager.persist(car.getTransmission());
        entityManager.persist(car.getChassis());
        entityManager.persist(car.getPerformance());
        entityManager.persist(car.getInsideDimensions());
        entityManager.persist(car.getOutsideDimensions());
        entityManager.persist(car);
        String username = "likeuser" + System.currentTimeMillis();
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("ReviewLike")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();
        testReview = Review.builder()
                .user(testUser)
                .car(car)
                .comment("Great car for testing likes")
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
                .status(ModerationStatus.APPROVED)
                .reviewDate(LocalDateTime.now())
                .build();
        entityManager.persist(testReview);

        entityManager.flush();
    }


    @Nested
    @DisplayName("POST /api/likes/review/{reviewId}")
    class ToggleLikeTests {

        @Test
        @DisplayName("toggles like when authenticated")
        void toggleLike_Authenticated_Returns200() throws Exception {
            performPostWithAuthNoBody(LIKE_BASE_URL + "/" + testReview.getId(), userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.liked").isBoolean())
                    .andExpect(jsonPath("$.likesCount").isNumber());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void toggleLike_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post(LIKE_BASE_URL + "/" + testReview.getId())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("toggles like status on repeated calls")
        void toggleLike_RepeatedCalls_TogglesStatus() throws Exception {
            String firstResponse = performPostWithAuthNoBody(LIKE_BASE_URL + "/" + testReview.getId(), userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            boolean firstLiked = objectMapper.readTree(firstResponse).get("liked").asBoolean();

            String secondResponse = performPostWithAuthNoBody(LIKE_BASE_URL + "/" + testReview.getId(), userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            boolean secondLiked = objectMapper.readTree(secondResponse).get("liked").asBoolean();

            assert firstLiked != secondLiked;
        }
    }


    @Nested
    @DisplayName("GET /api/likes/review/{reviewId}/status")
    class GetLikeStatusTests {

        @Test
        @DisplayName("returns like status when authenticated")
        void getLikeStatus_Authenticated_Returns200() throws Exception {
            performGetWithAuth(LIKE_BASE_URL + "/" + testReview.getId() + "/status", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.liked").isBoolean())
                    .andExpect(jsonPath("$.likesCount").isNumber());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getLikeStatus_NotAuthenticated_Returns401() throws Exception {
            performGetNoAuth(LIKE_BASE_URL + "/" + testReview.getId() + "/status")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns correct like count")
        void getLikeStatus_WithLikes_ReturnsCorrectCount() throws Exception {
            performPostWithAuthNoBody(LIKE_BASE_URL + "/" + testReview.getId(), userToken)
                    .andExpect(status().isOk());

            performGetWithAuth(LIKE_BASE_URL + "/" + testReview.getId() + "/status", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.liked").value(true))
                    .andExpect(jsonPath("$.likesCount").value(greaterThanOrEqualTo(1)));
        }
    }
}
