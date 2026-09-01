package com.carsplatform.backend.api.generations;

import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("GenerationRepository Integration Tests")
class GenerationRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private GenerationRepository generationRepository;

    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;

    @BeforeEach
    void setUp() {
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(testBrand);
        testModel = TestDataFactory.defaultModel(testBrand)
                .name("3 Series")
                .build();

        entityManager.persist(testModel);
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .name("E90")
                .build();

        entityManager.persist(testGeneration);

        entityManager.flush();
    }


    @Nested
    @DisplayName("FindById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("returns generation when exists")
        void findById_ExistingGeneration_ReturnsGeneration() {
            Optional<Generation> result = generationRepository.findById(testGeneration.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("E90");
        }

        @Test
        @DisplayName("returns empty when generation does not exist")
        void findById_NonExistingGeneration_ReturnsEmpty() {
            Optional<Generation> result = generationRepository.findById(UUID.randomUUID());
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("has correct model association")
        void findById_ExistingGeneration_HasCorrectModel() {
            Optional<Generation> result = generationRepository.findById(testGeneration.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getModel()).isNotNull();
            assertThat(result.get().getModel().getName()).isEqualTo("3 Series");
        }
    }


    @Nested
    @DisplayName("FindAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("returns all generations")
        void findAll_MultipleGenerations_ReturnsAllGenerations() {
            Generation gen2 = TestDataFactory.createGeneration(testModel, "F30");
            Generation gen3 = TestDataFactory.createGeneration(testModel, "G20");

            entityManager.persist(gen2);
            entityManager.persist(gen3);

            entityManager.flush();

            List<Generation> result = generationRepository.findAll();
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Generation::getName)
                    .containsExactlyInAnyOrder("E90", "F30", "G20");
        }

        @Test
        @DisplayName("generations from different models are separate")
        void findAll_GenerationsFromDifferentModels_ReturnsAll() {
            Model model2 = TestDataFactory.createModel(testBrand, "5 Series");
            entityManager.persist(model2);

            Generation gen5 = TestDataFactory.createGeneration(model2, "G30");
            entityManager.persist(gen5);

            entityManager.flush();

            List<Generation> result = generationRepository.findAll();
            assertThat(result).hasSize(2);
            assertThat(result).extracting(g -> g.getModel().getName())
                    .containsExactlyInAnyOrder("3 Series", "5 Series");
        }
    }


    @Nested
    @DisplayName("Save Tests")
    class SaveTests {

        @Test
        @DisplayName("persists new generation")
        void save_NewGeneration_PersistsGeneration() {
            Generation newGen = TestDataFactory.defaultGeneration(testModel)
                    .name("F80")
                    .build();

            Generation saved = generationRepository.save(newGen);
            entityManager.flush();
            assertThat(saved.getId()).isNotNull();
            Generation found = entityManager.find(Generation.class, saved.getId());
            assertThat(found.getName()).isEqualTo("F80");
        }

        @Test
        @DisplayName("updates existing generation")
        void save_ExistingGeneration_UpdatesGeneration() {
            testGeneration.setName("E90 LCI");

            generationRepository.save(testGeneration);
            entityManager.flush();
            entityManager.clear();
            Generation found = entityManager.find(Generation.class, testGeneration.getId());
            assertThat(found.getName()).isEqualTo("E90 LCI");
        }
    }


    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("removes generation")
        void delete_ExistingGeneration_RemovesGeneration() {
            generationRepository.delete(testGeneration);
            entityManager.flush();
            Generation found = entityManager.find(Generation.class, testGeneration.getId());
            assertThat(found).isNull();
        }
    }
}
