package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("FuelReportController Integration Tests")
class FuelReportControllerTest extends MockMvcTestBase {

    private static final String FUEL_REPORT_BASE_URL = "/api/fuel-reports";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Car testCar;
    private User testUser;
    private FuelReport testReport;
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

        // Create approved test fuel report
        testReport = FuelReport.builder()
                .user(testUser)
                .car(testCar)
                .fuelConsumption(new BigDecimal("7.5"))
                .comment("Normal driving conditions")
                .isApproved(true)
                .reportDate(LocalDateTime.now())
                .build();

        entityManager.persist(testReport);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/fuel-reports/{carId}")
    class GetFuelReportsTests {

        @Test
        @DisplayName("returns fuel reports for car (public endpoint)")
        void getFuelReports_ExistingCar_Returns200() throws Exception {

            // Perform GET request and verify response -> returns 200 OK and contains fuel reports
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns paginated results")
        void getFuelReports_WithPagination_ReturnsPaginated() throws Exception {

            // Perform GET request with pagination and verify response -> returns paginated results
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId() + "?page=0&size=5")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }

        @Test
        @DisplayName("returns reports with user info")
        void getFuelReports_ExistingCar_IncludesUserInfo() throws Exception {

            // Perform GET request and verify response -> returns reports with user info
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].usernameResponse.username").exists());
        }
    }


    @Nested
    @DisplayName("GET /api/fuel-reports/{carId}/average-consumption")
    class GetAverageConsumptionTests {

        @Test
        @DisplayName("returns average consumption for car (public endpoint)")
        void getAverageConsumption_ExistingCar_Returns200() throws Exception {

            // Perform GET request and verify response -> returns average consumption
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId() + "/average-consumption")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.averageFuelConsumption").exists());
        }
    }


    @Nested
    @DisplayName("POST /api/fuel-reports/{carId}")
    class CreateFuelReportTests {

        @Test
        @DisplayName("creates fuel report when authenticated")
        void createFuelReport_Authenticated_Returns201() throws Exception {

            // Create fuel report request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .comment("City driving")
                    .build();

            // Perform POST request with authentication and verify response -> returns 201 Created
            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void createFuelReport_NotAuthenticated_Returns403() throws Exception {

            // Create fuel report request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .comment("City driving")
                    .build();

            // Perform POST request without authentication and verify response -> returns 403 Forbidden
            performPostNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is missing")
        void createFuelReport_MissingConsumption_Returns400() throws Exception {

            // Create invalid fuel report request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .comment("City driving")
                    .build();

            // Perform POST request with authentication and verify response -> returns 400 Bad Request
            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is negative")
        void createFuelReport_NegativeConsumption_Returns400() throws Exception {

            // Create invalid fuel report request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("-5.0"))
                    .build();

            // Perform POST request with authentication and verify response -> returns 400 Bad Request
            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }
}
