package com.carsplatform.backend.api.bodyType;

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


@DisplayName("BodyTypeController Integration Tests")
class BodyTypeControllerTest extends MockMvcTestBase {

    private static final String BODY_TYPE_BASE_URL = "/api/body-types";

    @Autowired
    private EntityManager entityManager;

    private BodyType testBodyType1;
    private BodyType testBodyType2;

    @BeforeEach
    void setUp() {

        // Create test body types
        testBodyType1 = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();

        entityManager.persist(testBodyType1);

        testBodyType2 = TestDataFactory.defaultBodyType()
                .name("SUV")
                .build();

        entityManager.persist(testBodyType2);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/body-types")
    class GetAllBodyTypesTests {

        @Test
        @DisplayName("returns list of all body types (public endpoint)")
        void getAllBodyTypes_ReturnsAllBodyTypes() throws Exception {

            // Perform GET request and verify response is 200 OK with list of body types
            performGetNoAuth(BODY_TYPE_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        }

        @Test
        @DisplayName("returns body types with correct fields")
        void getAllBodyTypes_ReturnsBodyTypesWithCorrectFields() throws Exception {

            // Perform GET request and verify response is 200 OK with body types having correct fields
            performGetNoAuth(BODY_TYPE_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.name == 'Sedan')]").exists())
                    .andExpect(jsonPath("$[?(@.name == 'SUV')]").exists())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].name").exists());
        }

        @Test
        @DisplayName("returns empty list when no body types exist")
        void getAllBodyTypes_NoBodyTypes_ReturnsEmptyList() throws Exception {

            // Delete all body types and related data
            entityManager.createQuery("DELETE FROM Car").executeUpdate();
            entityManager.createQuery("DELETE FROM BodyType").executeUpdate();
            entityManager.flush();

            // Perform GET request and verify response is 200 OK with empty list
            performGetNoAuth(BODY_TYPE_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}
