package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.models.Model;
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


@DisplayName("GenerationController Integration Tests")
class GenerationControllerTest extends MockMvcTestBase {

    private static final String GENERATION_BASE_URL = "/api/generations";

    @Autowired
    private EntityManager entityManager;

    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;
    private BodyType testBodyType;
    private Car testCar;

    @BeforeEach
    void setUp() {
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .models(new java.util.ArrayList<>())
                .build();

        entityManager.persist(testBrand);
        testModel = TestDataFactory.defaultModel(testBrand)
                .name("3 Series")
                .generations(new java.util.ArrayList<>())
                .build();

        testBrand.getModels().add(testModel);
        entityManager.persist(testModel);
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .name("E90")
                .cars(new java.util.ArrayList<>())
                .build();

        testModel.getGenerations().add(testGeneration);
        entityManager.persist(testGeneration);
        testBodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(testBodyType);
        testCar = TestDataFactory.defaultCar(testGeneration, testBodyType)
                .name("320i")
                .build();

        testGeneration.getCars().add(testCar);

        entityManager.persist(testCar.getEngine());
        entityManager.persist(testCar.getTransmission());
        entityManager.persist(testCar.getChassis());
        entityManager.persist(testCar.getPerformance());
        entityManager.persist(testCar.getInsideDimensions());
        entityManager.persist(testCar.getOutsideDimensions());
        entityManager.persist(testCar);

        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/generations/{id}")
    class GetGenerationByIdTests {

        @Test
        @DisplayName("returns generation details when exists (public endpoint)")
        void getGenerationById_ExistingGeneration_ReturnsDetails() throws Exception {
            performGetNoAuth(GENERATION_BASE_URL + "/" + testGeneration.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("E90"))
                    .andExpect(jsonPath("$.cars").isArray());
        }

        @Test
        @DisplayName("returns generation with cars")
        void getGenerationById_ExistingGeneration_IncludesCars() throws Exception {
            performGetNoAuth(GENERATION_BASE_URL + "/" + testGeneration.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.cars", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.cars[?(@.name == '320i')]").exists());
        }

        @Test
        @DisplayName("returns generation with id field")
        void getGenerationById_ExistingGeneration_IncludesIdField() throws Exception {
            performGetNoAuth(GENERATION_BASE_URL + "/" + testGeneration.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testGeneration.getId().toString()))
                    .andExpect(jsonPath("$.name").value("E90"));
        }

        @Test
        @DisplayName("returns generation with model data")
        void getGenerationById_ExistingGeneration_IncludesModel() throws Exception {
            performGetNoAuth(GENERATION_BASE_URL + "/" + testGeneration.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.model").exists())
                    .andExpect(jsonPath("$.model.id").value(testModel.getId().toString()))
                    .andExpect(jsonPath("$.model.name").value("3 Series"));
        }

        @Test
        @DisplayName("returns generation with brand data")
        void getGenerationById_ExistingGeneration_IncludesBrand() throws Exception {
            performGetNoAuth(GENERATION_BASE_URL + "/" + testGeneration.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.brand").exists())
                    .andExpect(jsonPath("$.brand.id").value(testBrand.getId().toString()))
                    .andExpect(jsonPath("$.brand.name").value("BMW"));
        }

        @Test
        @DisplayName("returns 404 when generation does not exist")
        void getGenerationById_NonExistingGeneration_Returns404() throws Exception {
            String nonExistentId = UUID.randomUUID().toString();

            performGetNoAuth(GENERATION_BASE_URL + "/" + nonExistentId)
                    .andExpect(status().isNotFound());
        }
    }
}
