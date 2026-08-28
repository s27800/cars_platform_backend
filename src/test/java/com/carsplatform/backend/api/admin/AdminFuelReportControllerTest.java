package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
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

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("AdminFuelReportController Integration Tests")
class AdminFuelReportControllerTest extends MockMvcTestBase {

    private static final String ADMIN_FUEL_REPORTS_BASE_URL = "/api/admin/fuel-reports";
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
    private FuelReport pendingFuelReport;

    @BeforeEach
    void setUp() throws Exception {

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .name("Toyota")
                .build();

        entityManager.persist(brand);

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .name("Camry")
                .build();

        entityManager.persist(model);

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("XV70")
                .build();

        entityManager.persist(generation);

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .name("Camry 2.5")
                .build();

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        // Register regular user and get token
        String username = "fueluser" + System.currentTimeMillis();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("Fuel")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();

        // Register admin user and get token
        String adminUsername = "adminfuel" + System.currentTimeMillis();

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

        // Make the admin user an admin
        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        // Create pending fuel report
        pendingFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .isApproved(false)
                .build();

        entityManager.persist(pendingFuelReport);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/admin/fuel-reports/pending")
    class GetPendingFuelReportsTests {

        @Test
        @DisplayName("returns pending fuel reports when admin is authenticated")
        void getPendingFuelReports_Admin_ReturnsPendingFuelReports() throws Exception {

            // Perform GET request with admin authentication and verify response -> returns 200 OK and contains pending fuel report details
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.content[0].isApproved").value(false))
                    .andExpect(jsonPath("$.content[0].carInfo").exists())
                    .andExpect(jsonPath("$.content[0].carInfo.brandName").value("Toyota"));
        }

        @Test
        @DisplayName("returns 403 when regular user tries to access")
        void getPendingFuelReports_RegularUser_Returns403() throws Exception {

            // Perform GET request with regular user authentication and verify response -> returns 403 Forbidden
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void getPendingFuelReports_NotAuthenticated_Returns403() throws Exception {

            // Perform GET request without authentication and verify response -> returns 403 Forbidden
            performGetNoAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending")
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("PATCH /api/admin/fuel-reports/{id}/approve")
    class ApproveFuelReportTests {

        @Test
        @DisplayName("approves fuel report when admin is authenticated")
        void approveFuelReport_Admin_ApprovesFuelReport() throws Exception {

            // Perform GET request with admin authentication to verify pending fuel report exists and verify response -> returns 200 OK and contains pending fuel report details
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

            // Perform PATCH request with admin authentication to approve the fuel report and verify response -> returns 204 No Content
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("rejects fuel report when admin sets approve to false")
        void rejectFuelReport_Admin_RejectsFuelReport() throws Exception {

            // Perform PATCH request with admin authentication to reject the fuel report and verify response -> returns 204 No Content
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=false",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 403 when regular user tries to approve")
        void approveFuelReport_RegularUser_Returns403() throws Exception {

            // Perform PATCH request with regular user authentication to approve the fuel report and verify response -> returns 403 Forbidden
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true",
                    userToken
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void approveFuelReport_NotAuthenticated_Returns403() throws Exception {

            // Perform PATCH request without authentication to approve the fuel report and verify response -> returns 403 Forbidden
            performPatchNoAuthNoBody(ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 404 when fuel report does not exist")
        void approveFuelReport_FuelReportNotFound_Returns404() throws Exception {
            
            // Generate random UUID that doesn't exist
            String nonExistentId = UUID.randomUUID().toString();

            // Perform PATCH request for non-existent fuel report and verify response -> returns 404 Not Found
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + nonExistentId + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNotFound());
        }
    }
}
