package com.carsplatform.backend.api.fuelReportLikes;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
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

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("FuelReportLikeController Integration Tests")
class FuelReportLikeControllerTest extends MockMvcTestBase {

    private static final String FUEL_REPORT_LIKE_BASE_URL = "/api/likes/fuel-report";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private FuelReport testFuelReport;
    private User testUser;
    private String userToken;

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
        Car car = TestDataFactory.defaultCar(generation, bodyType)
                .name("320i")
                .build();

        entityManager.persist(car.getEngine());
        entityManager.persist(car.getTransmission());
        entityManager.persist(car.getChassis());
        entityManager.persist(car.getPerformance());
        entityManager.persist(car.getInsideDimensions());
        entityManager.persist(car.getOutsideDimensions());
        entityManager.persist(car);
        String username = "fuelreportlikeuser" + System.currentTimeMillis();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("FuelReport")
                .lastName("LikeUser")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, car)
                .status(ModerationStatus.APPROVED)
                .build();

        entityManager.persist(testFuelReport);

        entityManager.flush();
    }


    @Nested
    @DisplayName("POST /api/likes/fuel-report/{fuelReportId}")
    class ToggleLikeTests {

        @Test
        @DisplayName("toggles like when authenticated")
        void toggleLike_Authenticated_Returns200() throws Exception {
            performPostWithAuthNoBody(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId(), userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.liked").isBoolean())
                    .andExpect(jsonPath("$.likesCount").isNumber());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void toggleLike_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .post(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId())
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("toggles like status on repeated calls")
        void toggleLike_RepeatedCalls_TogglesStatus() throws Exception {
            String firstResponse = performPostWithAuthNoBody(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId(), userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            boolean firstLiked = objectMapper.readTree(firstResponse).get("liked").asBoolean();

            String secondResponse = performPostWithAuthNoBody(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId(), userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            boolean secondLiked = objectMapper.readTree(secondResponse).get("liked").asBoolean();

            assert firstLiked != secondLiked;
        }

        @Test
        @DisplayName("returns 404 when fuel report not found")
        void toggleLike_FuelReportNotFound_Returns404() throws Exception {
            String nonExistentId = UUID.randomUUID().toString();

            performPostWithAuthNoBody(FUEL_REPORT_LIKE_BASE_URL + "/" + nonExistentId, userToken)
                    .andExpect(status().isNotFound());
        }
    }


    @Nested
    @DisplayName("GET /api/likes/fuel-report/{fuelReportId}/status")
    class GetLikeStatusTests {

        @Test
        @DisplayName("returns like status when authenticated")
        void getLikeStatus_Authenticated_Returns200() throws Exception {
            performGetWithAuth(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId() + "/status", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.liked").isBoolean())
                    .andExpect(jsonPath("$.likesCount").isNumber());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getLikeStatus_NotAuthenticated_Returns401() throws Exception {
            performGetNoAuth(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId() + "/status")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns correct like count after multiple users like")
        void getLikeStatus_MultipleUsersLike_ReturnsCorrectCount() throws Exception {
            performPostWithAuthNoBody(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId(), userToken)
                    .andExpect(status().isOk());

            String response = performGetWithAuth(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId() + "/status", userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            boolean isLiked = objectMapper.readTree(response).get("liked").asBoolean();
            long likesCount = objectMapper.readTree(response).get("likesCount").asLong();

            assert isLiked;
            assert likesCount == 1;
        }

        @Test
        @DisplayName("returns not liked status when user hasn't liked")
        void getLikeStatus_UserNotLiked_ReturnsNotLiked() throws Exception {
            String response = performGetWithAuth(FUEL_REPORT_LIKE_BASE_URL + "/" + testFuelReport.getId() + "/status", userToken)
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            boolean isLiked = objectMapper.readTree(response).get("liked").asBoolean();
            long likesCount = objectMapper.readTree(response).get("likesCount").asLong();

            assert !isLiked;
            assert likesCount == 0;
        }
    }
}
