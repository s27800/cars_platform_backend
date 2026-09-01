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
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.ProposalCategory;
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

        String username = "proposaluser" + System.currentTimeMillis();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .username(username)
                .email(username + "@example.com")
                .password("Password123!")
                .firstName("Proposal")
                .lastName("User")
                .build();

        String response = performPostNoAuth(AUTH_BASE_URL + "/register", registerRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        userToken = objectMapper.readTree(response).get("accessToken").asText();
        testUser = userRepository.findByUsername(username).orElseThrow();
        testProposal = new DataProposal();

        testProposal.setUser(testUser);
        testProposal.setCar(testCar);
        testProposal.setCategory(ProposalCategory.ENGINE);
        testProposal.setComment("Engine power correction");
        testProposal.setStatus(ModerationStatus.PENDING);
        testProposal.setCreatedAt(LocalDateTime.now());

        entityManager.persist(testProposal);

        entityManager.flush();
    }


    @Nested
    @DisplayName("POST /api/data-proposals/{carId}")
    class CreateDataProposalTests {

        @Test
        @DisplayName("creates data proposal when authenticated")
        void createProposal_Authenticated_Returns201() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setComment("Proposed engine power update");
            request.setProposedValues(Map.of("maxPower", 225));

            performPostWithAuth(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("returns 401 when not authenticated")
        void createProposal_NotAuthenticated_Returns401() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();

            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of("maxPower", 225));

            performPostNoAuth(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request)
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 400 when proposed values are empty")
        void createProposal_EmptyValues_Returns400() throws Exception {
            CreateDataProposalRequest request = new CreateDataProposalRequest();
            request.setCategory(ProposalCategory.ENGINE);
            request.setProposedValues(Map.of());

            performPostWithAuth(DATA_PROPOSAL_BASE_URL + "/" + testCar.getId(), request, userToken)
                    .andExpect(status().isBadRequest());
        }
    }


    @Nested
    @DisplayName("GET /api/data-proposals/pending")
    class GetPendingProposalsTests {

        @Test
        @DisplayName("returns 401 when not authenticated")
        void getPendingProposals_NotAuthenticated_Returns401() throws Exception {
            performGetNoAuth(DATA_PROPOSAL_BASE_URL + "/pending")
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 403 when authenticated as regular user")
        void getPendingProposals_RegularUser_Returns403() throws Exception {
            performGetWithAuth(DATA_PROPOSAL_BASE_URL + "/pending", userToken)
                    .andExpect(status().isForbidden());
        }
    }


    @Nested
    @DisplayName("PATCH /api/data-proposals/{id}/resolve")
    class ResolveProposalTests {

        @Test
        @DisplayName("returns 401 when not authenticated")
        void resolveProposal_NotAuthenticated_Returns401() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch(DATA_PROPOSAL_BASE_URL + "/" + testProposal.getId() + "/resolve?approve=true")
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("returns 403 when authenticated as regular user")
        void resolveProposal_RegularUser_Returns403() throws Exception {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch(DATA_PROPOSAL_BASE_URL + "/" + testProposal.getId() + "/resolve?approve=true")
                            .header("Authorization", TestSecurityUtils.bearerToken(userToken))
                            .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
            ).andExpect(status().isForbidden());
        }
    }
}
