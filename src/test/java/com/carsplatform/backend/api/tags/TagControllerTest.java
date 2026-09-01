package com.carsplatform.backend.api.tags;

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


@DisplayName("TagController Integration Tests")
class TagControllerTest extends MockMvcTestBase {

    private static final String TAG_BASE_URL = "/api/tags";

    @Autowired
    private EntityManager entityManager;

    private Tag testTag1;
    private Tag testTag2;

    @BeforeEach
    void setUp() {
        testTag1 = TestDataFactory.defaultTag()
                .name("Sport")
                .build();
        entityManager.persist(testTag1);

        testTag2 = TestDataFactory.defaultTag()
                .name("Luxury")
                .build();
        entityManager.persist(testTag2);

        entityManager.flush();
    }


    @Nested
    @DisplayName("GET /api/tags")
    class GetAllTagsTests {

        @Test
        @DisplayName("returns list of all tags (public endpoint)")
        void getAllTags_ReturnsAllTags() throws Exception {
            performGetNoAuth(TAG_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        }

        @Test
        @DisplayName("returns tags with correct fields")
        void getAllTags_ReturnsTagsWithCorrectFields() throws Exception {
            performGetNoAuth(TAG_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[?(@.name == 'Sport')]").exists())
                    .andExpect(jsonPath("$[?(@.name == 'Luxury')]").exists())
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[0].name").exists());
        }

        @Test
        @DisplayName("returns empty list when no tags exist")
        void getAllTags_NoTags_ReturnsEmptyList() throws Exception {
            entityManager.createQuery("DELETE FROM Tag").executeUpdate();
            entityManager.flush();

            performGetNoAuth(TAG_BASE_URL)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}
