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
import com.carsplatform.backend.common.ModerationStatus;
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

        testReport = FuelReport.builder()
                .user(testUser)
                .car(testCar)
                .fuelConsumption(new BigDecimal("7.5"))
                .comment("Normal driving conditions")
                .status(ModerationStatus.APPROVED)
                .reportDate(LocalDateTime.now())
                .build();

        entityManager.persist(testReport);

        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/fuel-reports/{carId}")
    class GetFuelReportsTests {

        @Test
        @DisplayName("returns fuel reports for car (public endpoint)")
        void getFuelReports_ExistingCar_Returns200() throws Exception {
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns paginated results")
        void getFuelReports_WithPagination_ReturnsPaginated() throws Exception {
            performGetNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId() + "?page=0&size=5")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }

        @Test
        @DisplayName("returns reports with user info")
        void getFuelReports_ExistingCar_IncludesUserInfo() throws Exception {
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
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .comment("City driving")
                    .build();

            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void createFuelReport_NotAuthenticated_Returns401() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .comment("City driving")
                    .build();

            performPostNoAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is missing")
        void createFuelReport_MissingConsumption_Returns400() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .comment("City driving")
                    .build();

            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 400 when fuel consumption is negative")
        void createFuelReport_NegativeConsumption_Returns400() throws Exception {
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("-5.0"))
                    .build();

            performPostWithAuth(FUEL_REPORT_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }
}
