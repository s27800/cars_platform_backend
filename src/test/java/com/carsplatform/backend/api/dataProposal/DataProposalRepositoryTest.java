package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.ProposalCategory;
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

        entityManager.flush();
    }


    @Nested
    @DisplayName("findByStatus")
    class FindByStatusTests {

        @Test
        @DisplayName("returns only proposals with matching status")
        void withPendingStatus_ReturnsOnlyPending() {
            DataProposal pending1 = createProposal(ModerationStatus.PENDING);
            DataProposal pending2 = createProposal(ModerationStatus.PENDING);
            DataProposal approved = createProposal(ModerationStatus.APPROVED);

            entityManager.persist(pending1);
            entityManager.persist(pending2);
            entityManager.persist(approved);

            entityManager.flush();

            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    ModerationStatus.PENDING, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).allMatch(p -> p.getStatus() == ModerationStatus.PENDING);
        }

        @Test
        @DisplayName("returns empty when no matching status")
        void noMatchingStatus_ReturnsEmpty() {
            DataProposal pending = createProposal(ModerationStatus.PENDING);

            entityManager.persist(pending);
            entityManager.flush();

            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    ModerationStatus.APPROVED, PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("supports pagination")
        void withPagination_ReturnsPaginatedResults() {
            for (int i = 0; i < 5; i++) {
                DataProposal proposal = createProposal(ModerationStatus.PENDING);
                entityManager.persist(proposal);
            }

            entityManager.flush();

            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    ModerationStatus.PENDING, PageRequest.of(0, 3));
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(5);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("eagerly loads car relation")
        void eagerLoading_LoadsCarRelation() {
            DataProposal proposal = createProposal(ModerationStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();
            entityManager.clear();

            Page<DataProposal> result = dataProposalRepository.findByStatus(
                    ModerationStatus.PENDING, PageRequest.of(0, 10));
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
            DataProposal userProposal = createProposal(ModerationStatus.PENDING);

            entityManager.persist(userProposal);

            User otherUser = TestDataFactory.createUser("other");

            entityManager.persist(otherUser);

            DataProposal otherProposal = new DataProposal();

            otherProposal.setUser(otherUser);
            otherProposal.setCar(testCar);
            otherProposal.setCategory(ProposalCategory.ENGINE);
            otherProposal.setStatus(ModerationStatus.PENDING);
            otherProposal.setCreatedAt(LocalDateTime.now());

            entityManager.persist(otherProposal);
            entityManager.flush();

            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUser().getId()).isEqualTo(testUser.getId());
        }

        @Test
        @DisplayName("returns empty when user has no proposals")
        void noProposals_ReturnsEmpty() {
            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("eagerly loads car relation")
        void eagerLoading_LoadsCarRelation() {
            DataProposal proposal = createProposal(ModerationStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();
            entityManager.clear();

            Page<DataProposal> result = dataProposalRepository.findByUser(
                    testUser, PageRequest.of(0, 10));
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
            DataProposal proposal = createProposal(ModerationStatus.PENDING);

            DataProposal saved = dataProposalRepository.save(proposal);

            entityManager.flush();
            assertThat(saved.getId()).isNotNull();
            assertThat(entityManager.find(DataProposal.class, saved.getId())).isNotNull();
        }

        @Test
        @DisplayName("save updates existing proposal")
        void save_ExistingProposal_UpdatesProposal() {
            DataProposal proposal = createProposal(ModerationStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();

            proposal.setStatus(ModerationStatus.APPROVED);
            proposal.setAdminComment("Approved by admin");

            dataProposalRepository.save(proposal);
            entityManager.flush();
            entityManager.clear();
            DataProposal found = entityManager.find(DataProposal.class, proposal.getId());

            assertThat(found.getStatus()).isEqualTo(ModerationStatus.APPROVED);
            assertThat(found.getAdminComment()).isEqualTo("Approved by admin");
        }

        @Test
        @DisplayName("delete removes proposal")
        void delete_ExistingProposal_RemovesProposal() {
            DataProposal proposal = createProposal(ModerationStatus.PENDING);

            entityManager.persist(proposal);
            entityManager.flush();

            dataProposalRepository.delete(proposal);
            entityManager.flush();
            DataProposal found = entityManager.find(DataProposal.class, proposal.getId());

            assertThat(found).isNull();
        }
    }


    private DataProposal createProposal(ModerationStatus status) {
        DataProposal proposal = new DataProposal();

        proposal.setUser(testUser);
        proposal.setCar(testCar);
        proposal.setCategory(ProposalCategory.ENGINE);
        proposal.setComment("Proposed engine change");
        proposal.setStatus(status);
        proposal.setCreatedAt(LocalDateTime.now());

        return proposal;
    }
}
