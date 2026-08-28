package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.bodyType.BodyTypeRepository;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.brands.BrandRepository;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.generations.GenerationRepository;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.models.ModelRepository;
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


@DisplayName("CarController Integration Tests")
class CarControllerTest extends MockMvcTestBase {

    private static final String CAR_BASE_URL = "/api/cars";

    @Autowired
    private EntityManager entityManager;

    private Car testCar;
    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;
    private BodyType testBodyType;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(testBrand);

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .name("3 Series")
                .build();

        entityManager.persist(testModel);

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .name("E90")
                .build();

        entityManager.persist(testGeneration);

        // Create test body type
        testBodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(testBodyType);

        // Create test car
        testCar = TestDataFactory.defaultCar(testGeneration, testBodyType)
                .name("320i")
                .build();

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/cars/{id}")
    class GetCarByIdTests {

        @Test
        @DisplayName("returns car details when exists (public endpoint)")
        void getCarById_ExistingCar_Returns200() throws Exception {

            // Perform GET request and verify results -> status 200 OK and returns car details
            performGetNoAuth(CAR_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("320i"))
                    .andExpect(jsonPath("$.engine").exists())
                    .andExpect(jsonPath("$.transmission").exists())
                    .andExpect(jsonPath("$.chassis").exists())
                    .andExpect(jsonPath("$.performance").exists());
        }

        @Test
        @DisplayName("returns 404 when car does not exist")
        void getCarById_NonExistingCar_Returns404() throws Exception {

            // Use random UUID that doesn't exist
            String nonExistentId = UUID.randomUUID().toString();

            // Perform GET request and verify results -> status 404 Not Found when car does not exist
            performGetNoAuth(CAR_BASE_URL + "/" + nonExistentId)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("includes brand information in response")
        void getCarById_ExistingCar_IncludesBrandInfo() throws Exception {

            // Perform GET request and verify results -> status 200 OK and includes brand info
            performGetNoAuth(CAR_BASE_URL + "/" + testCar.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.brand.name").value("BMW"));
        }
    }


    @Nested
    @DisplayName("GET /api/cars/search")
    class SearchCarsTests {

        @Test
        @DisplayName("returns all cars when no filters (public endpoint)")
        void searchCars_NoFilters_ReturnsAllCars() throws Exception {

            // Perform GET request and verify results -> status 200 OK and returns all cars
            performGetNoAuth(CAR_BASE_URL + "/search")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns paginated results")
        void searchCars_WithPagination_ReturnsPaginatedResults() throws Exception {

            // Perform GET request and verify results -> status 200 OK and returns paginated results
            performGetNoAuth(CAR_BASE_URL + "/search?page=0&size=5")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }

        @Test
        @DisplayName("filters by brand ID")
        void searchCars_ByBrandId_ReturnsFilteredCars() throws Exception {

            // Perform GET request and verify results -> status 200 OK and filters by brand ID
            performGetNoAuth(CAR_BASE_URL + "/search?brandIds=" + testBrand.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[*].generation.model.brand.name",
                            everyItem(equalTo("BMW"))));
        }

        @Test
        @DisplayName("filters by body type ID")
        void searchCars_ByBodyTypeId_ReturnsFilteredCars() throws Exception {

            // Perform GET request and verify results -> status 200 OK and filters by body type ID
            performGetNoAuth(CAR_BASE_URL + "/search?bodyTypeIds=" + testBodyType.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("filters by engine type")
        void searchCars_ByEngineType_ReturnsFilteredCars() throws Exception {

            // Perform GET request and verify results -> status 200 OK and filters by engine type
            performGetNoAuth(CAR_BASE_URL + "/search?engineTypes=" + testCar.getEngine().getEngineType())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("filters by power range")
        void searchCars_ByPowerRange_ReturnsFilteredCars() throws Exception {

            // Prepare filters
            int minPower = testCar.getEngine().getMaxPower() - 50;
            int maxPower = testCar.getEngine().getMaxPower() + 50;

            // Perform GET request and verify results -> status 200 OK and filters by power range
            performGetNoAuth(CAR_BASE_URL + "/search?minPower=" + minPower + "&maxPower=" + maxPower)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("returns empty content when no matches")
        void searchCars_NoMatches_ReturnsEmptyContent() throws Exception {

            // Use random UUID that doesn't exist
            String nonExistentId = UUID.randomUUID().toString();

            // Perform GET request and verify results -> status 200 OK and returns empty content
            performGetNoAuth(CAR_BASE_URL + "/search?brandIds=" + nonExistentId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }

        @Test
        @DisplayName("combines multiple filters")
        void searchCars_MultipleFilters_ReturnsFilteredCars() throws Exception {

            // Perform GET request and verify results -> status 200 OK and filters by multiple criteria
            performGetNoAuth(CAR_BASE_URL + "/search?brandIds=" + testBrand.getId() +
                      "&bodyTypeIds=" + testBodyType.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }
}
