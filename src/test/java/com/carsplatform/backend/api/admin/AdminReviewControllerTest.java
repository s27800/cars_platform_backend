package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("AdminReviewController Integration Tests")
class AdminReviewControllerTest extends MockMvcTestBase {

    private static final String ADMIN_REVIEWS_BASE_URL = "/api/admin/reviews";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Car testCar;
    private User testUser;
    private User adminUser;
    private String userToken;
    private String adminToken;
    private Review pendingReview;

    @BeforeEach
    void setUp() throws Exception {
        Brand brand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(brand);
        Model model = TestDataFactory.defaultModel(brand)
                .name("3 Series")
                .build();

        entityManager.persist(model);
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("E90")
                .build();

        entityManager.persist(generation);
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);
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

        String adminUsername = "adminreview" + System.currentTimeMillis();

        RegisterRequest adminRegisterRequest = RegisterRequest.builder()
                .username(adminUsername)
                .email(adminUsername + "@example.com")
                .password("AdminPassword123!")
                .firstName("Admin")
                .lastName("User")
                .build();

        String adminResponse = performPostNoAuth(AUTH_BASE_URL + "/register", adminRegisterRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        adminToken = objectMapper.readTree(adminResponse).get("accessToken").asText();
        adminUser = userRepository.findByUsername(adminUsername).orElseThrow();

        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        pendingReview = TestDataFactory.defaultReview(testUser, testCar)
                .status(ModerationStatus.PENDING)
                .build();

        entityManager.persist(pendingReview);

        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/admin/reviews/pending")
    class GetPendingReviewsTests {

        @Test
        @DisplayName("returns pending reviews when admin is authenticated")
        void getPendingReviews_Admin_ReturnsPendingReviews() throws Exception {
            performGetWithAuth(ADMIN_REVIEWS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                    .andExpect(jsonPath("$.content[0].carInfo").exists())
                    .andExpect(jsonPath("$.content[0].carInfo.brandName").value("BMW"));
        }

        @Test
        @DisplayName("returns 403 when regular user tries to access")
        void getPendingReviews_RegularUser_Returns403() throws Exception {
            performGetWithAuth(ADMIN_REVIEWS_BASE_URL + "/pending", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getPendingReviews_NotAuthenticated_Returns401() throws Exception {
            performGetNoAuth(ADMIN_REVIEWS_BASE_URL + "/pending")
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("PATCH /api/admin/reviews/{id}/approve")
    class ApproveReviewTests {

        @Test
        @DisplayName("approves review when admin is authenticated")
        void approveReview_Admin_ApprovesReview() throws Exception {
            performGetWithAuth(ADMIN_REVIEWS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

            performPatchWithAuthNoBody(
                    ADMIN_REVIEWS_BASE_URL + "/" + pendingReview.getId() + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("rejects review when admin sets approve to false")
        void rejectReview_Admin_RejectsReview() throws Exception {
            performPatchWithAuthNoBody(
                    ADMIN_REVIEWS_BASE_URL + "/" + pendingReview.getId() + "/approve?approve=false",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 403 when regular user tries to approve")
        void approveReview_RegularUser_Returns403() throws Exception {
            performPatchWithAuthNoBody(
                    ADMIN_REVIEWS_BASE_URL + "/" + pendingReview.getId() + "/approve?approve=true",
                    userToken
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void approveReview_NotAuthenticated_Returns401() throws Exception {
            performPatchNoAuthNoBody(ADMIN_REVIEWS_BASE_URL + "/" + pendingReview.getId() + "/approve?approve=true")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 404 when review does not exist")
        void approveReview_ReviewNotFound_Returns404() throws Exception {
            String nonExistentId = UUID.randomUUID().toString();

            performPatchWithAuthNoBody(
                    ADMIN_REVIEWS_BASE_URL + "/" + nonExistentId + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNotFound());
        }
    }
}
