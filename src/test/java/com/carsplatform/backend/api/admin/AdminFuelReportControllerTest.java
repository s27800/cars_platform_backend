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
        Brand brand = TestDataFactory.defaultBrand()
                .name("Toyota")
                .build();

        entityManager.persist(brand);
        Model model = TestDataFactory.defaultModel(brand)
                .name("Camry")
                .build();

        entityManager.persist(model);
        Generation generation = TestDataFactory.defaultGeneration(model)
                .name("XV70")
                .build();

        entityManager.persist(generation);
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(bodyType);
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

        adminUser.setIsAdmin(true);
        userRepository.save(adminUser);

        pendingFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .status(ModerationStatus.PENDING)
                .build();

        entityManager.persist(pendingFuelReport);

        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/admin/fuel-reports/pending")
    class GetPendingFuelReportsTests {

        @Test
        @DisplayName("returns pending fuel reports when admin is authenticated")
        void getPendingFuelReports_Admin_ReturnsPendingFuelReports() throws Exception {
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.content[0].status").value("PENDING"))
                    .andExpect(jsonPath("$.content[0].carInfo").exists())
                    .andExpect(jsonPath("$.content[0].carInfo.brandName").value("Toyota"));
        }

        @Test
        @DisplayName("returns 403 when regular user tries to access")
        void getPendingFuelReports_RegularUser_Returns403() throws Exception {
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", userToken)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getPendingFuelReports_NotAuthenticated_Returns401() throws Exception {
            performGetNoAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending")
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("PATCH /api/admin/fuel-reports/{id}/approve")
    class ApproveFuelReportTests {

        @Test
        @DisplayName("approves fuel report when admin is authenticated")
        void approveFuelReport_Admin_ApprovesFuelReport() throws Exception {
            performGetWithAuth(ADMIN_FUEL_REPORTS_BASE_URL + "/pending", adminToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));

            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("rejects fuel report when admin sets approve to false")
        void rejectFuelReport_Admin_RejectsFuelReport() throws Exception {
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=false",
                    adminToken
            ).andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("returns 403 when regular user tries to approve")
        void approveFuelReport_RegularUser_Returns403() throws Exception {
            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true",
                    userToken
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void approveFuelReport_NotAuthenticated_Returns401() throws Exception {
            performPatchNoAuthNoBody(ADMIN_FUEL_REPORTS_BASE_URL + "/" + pendingFuelReport.getId() + "/approve?approve=true")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 404 when fuel report does not exist")
        void approveFuelReport_FuelReportNotFound_Returns404() throws Exception {
            String nonExistentId = UUID.randomUUID().toString();

            performPatchWithAuthNoBody(
                    ADMIN_FUEL_REPORTS_BASE_URL + "/" + nonExistentId + "/approve?approve=true",
                    adminToken
            ).andExpect(status().isNotFound());
        }
    }
}
