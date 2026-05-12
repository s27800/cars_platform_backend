package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.models.Model;
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


@DisplayName("BrandController Integration Tests")
class BrandControllerTest extends MockMvcTestBase {

    private static final String BRAND_BASE_URL = "/api/brands";

    @Autowired
    private EntityManager entityManager;

    private Brand testBrand;
    private Model testModel;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .models(new java.util.ArrayList<>())
                .build();
        entityManager.persist(testBrand);

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .name("3 Series")
                .description("Compact executive car")
                .generations(new java.util.ArrayList<>())
                .build();
        testBrand.getModels().add(testModel);
        entityManager.persist(testModel);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/brands")
    class GetAllBrandsTests {

        @Test
        @DisplayName("returns list of all brands (public endpoint)")
        void getAllBrands_ReturnsAllBrands() throws Exception {

            // Perform GET request and verify results -> correct status and returns list of brands
            performGet(BRAND_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns brand with correct fields")
        void getAllBrands_ReturnsBrandWithCorrectFields() throws Exception {

            // Perform GET request and verify results -> correct status and brands contain correct fields
            performGet(BRAND_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.name == 'BMW')]").exists())
                    .andExpect(jsonPath("$[?(@.name == 'BMW')].id").exists());
        }
    }


    @Nested
    @DisplayName("GET /api/brands/{id}")
    class GetBrandByIdTests {

        @Test
        @DisplayName("returns brand details when exists (public endpoint)")
        void getBrandById_ExistingBrand_Returns200() throws Exception {

            // Perform GET request and verify results -> correct status and returns brand details
            performGet(BRAND_BASE_URL + "/" + testBrand.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("BMW"))
                    .andExpect(jsonPath("$.models").isArray());
        }

        @Test
        @DisplayName("returns brand with models")
        void getBrandById_ExistingBrand_IncludesModels() throws Exception {

            // Perform GET request and verify results -> correct status and brand includes models
            performGet(BRAND_BASE_URL + "/" + testBrand.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.models", hasSize(greaterThanOrEqualTo(1))))
                    .andExpect(jsonPath("$.models[?(@.name == '3 Series')]").exists());
        }

        @Test
        @DisplayName("returns 404 when brand does not exist")
        void getBrandById_NonExistingBrand_Returns404() throws Exception {

            // Perform GET request and verify results -> 404 brand does not exist
            performGet(BRAND_BASE_URL + "/999999")
                    .andExpect(status().isNotFound());
        }
    }
}
