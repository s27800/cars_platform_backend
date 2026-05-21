package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("ModelController Integration Tests")
class ModelControllerTest extends MockMvcTestBase {

    private static final String MODEL_BASE_URL = "/api/models";

    @Autowired
    private EntityManager entityManager;

    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;

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
                .description("Compact executive car")
                .generations(new java.util.ArrayList<>())
                .build();

        entityManager.persist(testModel);

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .name("E90")
                .build();

        testModel.getGenerations().add(testGeneration);
        entityManager.persist(testGeneration);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/models/{id}")
    class GetModelByIdTests {

        @Test
        @DisplayName("returns model details when exists (public endpoint)")
        void getModelById_ExistingModel_Returns200() throws Exception {

            // Perform GET request and verify response -> 200 OK with correct model details
            performGetNoAuth(MODEL_BASE_URL + "/" + testModel.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("3 Series"))
                    .andExpect(jsonPath("$.description").value("Compact executive car"))
                    .andExpect(jsonPath("$.generations").isArray());
        }

        @Test
        @DisplayName("returns model with generations")
        void getModelById_ExistingModel_IncludesGenerations() throws Exception {

            // Perform GET request and verify response -> 200 OK with generations included
            performGetNoAuth(MODEL_BASE_URL + "/" + testModel.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.generations", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.generations[?(@.name == 'E90')]").exists());
        }

        @Test
        @DisplayName("returns model with id field")
        void getModelById_ExistingModel_IncludesIdField() throws Exception {

            // Perform GET request and verify response -> 200 OK with id field included
            performGetNoAuth(MODEL_BASE_URL + "/" + testModel.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testModel.getId()))
                    .andExpect(jsonPath("$.name").value("3 Series"));
        }

        @Test
        @DisplayName("returns 404 when model does not exist")
        void getModelById_NonExistingModel_Returns404() throws Exception {

            // Perform GET request and verify response -> 404 Not Found
            performGetNoAuth(MODEL_BASE_URL + "/999999")
                    .andExpect(status().isNotFound());
        }
    }
}
