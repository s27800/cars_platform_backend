package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.authentication.dtos.RegisterRequest;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.MockMvcTestBase;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.TestSecurityUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@DisplayName("DataProposalController Integration Tests")
class DataProposalControllerTest extends MockMvcTestBase {

    private static final String DATA_PROPOSAL_BASE_URL = "/api/data-proposals";
    private static final String AUTH_BASE_URL = "/api/auth";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Car testCar;
    private User testUser;
    private String userToken;
    private DataProposal testProposal;

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
        String username = "proposaluser" + System.currentTimeMillis();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("Proposal")
                .lastName("User")
                .build();

        String response = performPost(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();

        // Create test proposal
        testProposal = new DataProposal();

        testProposal.setUser(testUser);
        testProposal.setCar(testCar);
        testProposal.setCategory("engine");
        testProposal.setComment("Engine power correction");
        testProposal.setStatus(DataProposalStatus.PENDING);
        testProposal.setCreatedAt(LocalDateTime.now());

        entityManager.persist(testProposal);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("POST /api/data-proposals/{carId}")
    class CreateDataProposalTests {

        @Test
        @DisplayName("creates data proposal when authenticated")
        void createProposal_Authenticated_Returns201() throws Exception {

            // Create request with valid proposal data
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("engine");
            request.setComment("Proposed engine power update");
            request.setProposedValues(Map.of("maxPower", 225));

            // Perform POST request with authentication and verify results -> 201 Created is returned
            performPostWithAuth(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void createProposal_NotAuthenticated_Returns403() throws Exception {

            // Create request without authentication
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory("engine");
            request.setProposedValues(Map.of("maxPower", 225));

            // Perform POST request without authentication and verify results -> 403 Forbidden is returned
            performPost(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request)
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 400 when proposed values are empty")
        void createProposal_EmptyValues_Returns400() throws Exception {

            // Create request with empty proposed values
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory("engine");
            request.setProposedValues(Map.of());

            // Perform POST request with authentication and verify results -> 400 Bad Request is returned
            performPostWithAuth(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("GET /api/data-proposals/me")
    class GetMyProposalsTests {

        @Test
        @DisplayName("returns proposals when authenticated")
        void getMyProposals_Authenticated_ReturnsProposals() throws Exception {

            // Perform GET request with authentication and verify results -> 200 OK is returned with proposals
            performGetWithAuth(DATA_PROPOSAL_BASE_URL + "/me", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
        }

        @Test
        @DisplayName("returns 403 when not authenticated")
        void getMyProposals_NotAuthenticated_Returns403() throws Exception {

            // Perform GET request without authentication and verify results -> 403 Forbidden is returned
            performGet(DATA_PROPOSAL_BASE_URL + "/me")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns paginated results")
        void getMyProposals_WithPagination_ReturnsPaginated() throws Exception {

            // Perform GET request with authentication and pagination parameters and verify results -> 200 OK is returned
            performGetWithAuth(DATA_PROPOSAL_BASE_URL + "/me?page=0&size=5", userToken)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                    .andExpect(jsonPath("$.pageable.pageSize").value(5));
        }
    }


    @Nested
    @DisplayName("GET /api/data-proposals/pending")
    class GetPendingProposalsTests {

        @Test
        @DisplayName("returns 403 when not authenticated")
        void getPendingProposals_NotAuthenticated_Returns403() throws Exception {

            // Perform GET request without authentication and verify results -> 403 Forbidden is returned
            performGet(DATA_PROPOSAL_BASE_URL + "/pending")
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 403 when authenticated as regular user")
        void getPendingProposals_RegularUser_Returns403() throws Exception {

            // Perform GET request with authentication as regular user and verify results -> 403 Forbidden is returned
            performGetWithAuth(DATA_PROPOSAL_BASE_URL + "/pending", userToken)
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("PATCH /api/data-proposals/resolve/{id}")
    class ResolveProposalTests {

        @Test
        @DisplayName("returns 403 when not authenticated")
        void resolveProposal_NotAuthenticated_Returns403() throws Exception {

            // Perform PATCH request without authentication and verify results -> 403 Forbidden is returned
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch(DATA_PROPOSAL_BASE_URL + "/resolve/" + testProposal.getId() + "?approve=true")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("returns 403 when authenticated as regular user")
        void resolveProposal_RegularUser_Returns403() throws Exception {

            // Perform PATCH request with authentication as regular user and verify results -> 403 Forbidden is returned
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch(DATA_PROPOSAL_BASE_URL + "/resolve/" + testProposal.getId() + "?approve=true")
                            .header("Authorization", TestSecurityUtils.bearerToken(userToken))
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }
    }
}
