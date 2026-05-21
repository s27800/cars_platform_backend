package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("DataProposalRepository Integration Tests")
class DataProposalRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DataProposalRepository dataProposalRepository;

    private User testUser;
    private User adminUser;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test users
        testUser = TestDataFactory.defaultUser()
                .username("proposer")
                .email("proposer@example.com")
                .build();

        entityManager.persist(testUser);

        adminUser = TestDataFactory.adminUser()
                .username("admin")
                .email("admin@example.com")
                .build();

        entityManager.persist(adminUser);

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

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findByStatus")
    class FindByStatusTests {

        @Test
        @DisplayName("returns only proposals with matching status")
        void withPendingStatus_ReturnsOnlyPending() {

            // Create proposals with different statuses
            DataProposal pending1 = createProposal(DataProposalStatus.PENDING);
            DataProposal pending2 = createProposal(DataProposalStatus.PENDING);
            DataProposal approved = createProposal(DataProposalStatus.APPROVED);

            entityManager.persist(pending1);
            entityManager.persist(pending2);
            entityManager.persist(approved);

            entityManager.flush();

            // Find proposals with PENDING status
            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    DataProposalStatus.PENDING, PageRequest.of(0, 10));

            // Verify results -> only pending proposals are returned
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).allMatch(p -> p.getStatus() == DataProposalStatus.PENDING);
        }

        @Test
        @DisplayName("returns empty when no matching status")
        void noMatchingStatus_ReturnsEmpty() {

            // Create a proposal with PENDING status
            DataProposal pending = createProposal(DataProposalStatus.PENDING);

            entityManager.persist(pending);
            entityManager.flush();

            // Find proposals with APPROVED status
            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    DataProposalStatus.APPROVED, PageRequest.of(0, 10));

            // Verify results -> no proposals are returned
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("supports pagination")
        void withPagination_ReturnsPaginatedResults() {

            // Create proposals with PENDING status
            for (int i = 0; i < 5; i++) {
                DataProposal proposal = createProposal(DataProposalStatus.PENDING);
                entityManager.persist(proposal);
            }

            entityManager.flush();

            // Find proposals with PENDING status and apply pagination
            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    DataProposalStatus.PENDING, PageRequest.of(0, 3));

            // Verify results -> only paginated proposals are returned
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("eagerly loads car relation")
        void eagerLoading_LoadsCarRelation() {

            // Create a proposal with PENDING status
            DataProposal proposal = createProposal(DataProposalStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();
            entityManager.clear();

            // Find proposals with PENDING status
            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    DataProposalStatus.PENDING, PageRequest.of(0, 10));

            // Verify results -> only pending proposals are returned
            assertThat(result.getContent()).hasSize(1);

            DataProposal found = result.getContent().get(0);

            assertThat(found.getCar()).isNotNull();
            assertThat(found.getCar().getName()).isEqualTo("320i");
        }
    }


    @Nested
    @DisplayName("findByUser")
    class FindByUserTests {

        @Test
        @DisplayName("returns only user's proposals")
        void withOtherUsersProposals_ReturnsOnlyOwnProposals() {

            // Create a proposal with PENDING status for the test user
            DataProposal userProposal = createProposal(DataProposalStatus.PENDING);

            entityManager.persist(userProposal);

            // Create other user
            User otherUser = TestDataFactory.createUser("other");

            entityManager.persist(otherUser);

            // Create a proposal with PENDING status for the other user
            DataProposal otherProposal = new DataProposal();

            otherProposal.setUser(otherUser);
            otherProposal.setCar(testCar);
            otherProposal.setCategory("engine");
            otherProposal.setStatus(DataProposalStatus.PENDING);
            otherProposal.setCreatedAt(LocalDateTime.now());

            entityManager.persist(otherProposal);
            entityManager.flush();

            // Find proposals for the test user
            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));

            // Verify results -> only test user's proposals are returned
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(testUser.getId());
        }

        @Test
        @DisplayName("returns empty when user has no proposals")
        void noProposals_ReturnsEmpty() {

            // Find proposals for the test user
            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));

            // Verify results -> no proposals are returned
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("eagerly loads car relation")
        void eagerLoading_LoadsCarRelation() {

            // Create a proposal with PENDING status for the test user
            DataProposal proposal = createProposal(DataProposalStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();
            entityManager.clear();

            // Find proposals for the test user
            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));

            // Verify results -> only test user's proposals are returned
            assertThat(result.getContent()).hasSize(1);

            DataProposal found = result.getContent().get(0);

            assertThat(found.getCar()).isNotNull();
            assertThat(found.getCar().getName()).isEqualTo("320i");
        }
    }


    @Nested
    @DisplayName("CRUD Operations")
    class CrudTests {

        @Test
        @DisplayName("save persists new proposal")
        void save_NewProposal_PersistsProposal() {

            // Create proposal
            DataProposal proposal = createProposal(DataProposalStatus.PENDING);

            // Save proposal
            DataProposal saved = dataProposalRepository.save(proposal);

            entityManager.flush();

            // Verify results -> proposal is saved
            assertThat(saved.getId()).isNotNull();
            assertThat(entityManager.find(DataProposal.class, saved.getId())).isNotNull();
        }

        @Test
        @DisplayName("save updates existing proposal")
        void save_ExistingProposal_UpdatesProposal() {

            // Create proposal
            DataProposal proposal = createProposal(DataProposalStatus.PENDING);

            // Save proposal
            entityManager.persist(proposal);
            entityManager.flush();

            // Update proposal
            proposal.setStatus(DataProposalStatus.APPROVED);
            proposal.setAdminComment("Approved by admin");

            // Save updated proposal
            dataProposalRepository.save(proposal);
            entityManager.flush();
            entityManager.clear();

            // Verify results -> proposal is updated
            DataProposal found = entityManager.find(DataProposal.class, proposal.getId());

            assertThat(found.getStatus()).isEqualTo(DataProposalStatus.APPROVED);
            assertThat(found.getAdminComment()).isEqualTo("Approved by admin");
        }

        @Test
        @DisplayName("delete removes proposal")
        void delete_ExistingProposal_RemovesProposal() {

            // Create proposal
            DataProposal proposal = createProposal(DataProposalStatus.PENDING);

            // Save proposal
            entityManager.persist(proposal);
            entityManager.flush();

            // Delete proposal
            dataProposalRepository.delete(proposal);
            entityManager.flush();

            // Verify results -> proposal is deleted
            DataProposal found = entityManager.find(DataProposal.class, proposal.getId());

            assertThat(found).isNull();
        }
    }


    // helper method

    private DataProposal createProposal(DataProposalStatus status) {
        DataProposal proposal = new DataProposal();

        proposal.setUser(testUser);
        proposal.setCar(testCar);
        proposal.setCategory("engine");
        proposal.setComment("Proposed engine change");
        proposal.setStatus(status);
        proposal.setCreatedAt(LocalDateTime.now());

        return proposal;
    }
}
