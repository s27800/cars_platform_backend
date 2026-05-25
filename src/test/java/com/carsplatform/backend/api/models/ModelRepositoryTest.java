package com.carsplatform.backend.api.models;

import com.carsplatform.backend.api.brands.Brand;
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

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("ModelRepository Integration Tests")
class ModelRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ModelRepository modelRepository;

    private Brand testBrand;
    private Model testModel;

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
                .build();

        entityManager.persist(testModel);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("findById returns model when exists")
        void findById_ExistingModel_ReturnsModel() {

            // Find model by ID
            Optional<Model> result = modelRepository.findById(testModel.getId());

            // Verify result -> model exists and has correct name
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("3 Series");
        }

        @Test
        @DisplayName("model has correct brand association")
        void findById_ExistingModel_HasCorrectBrand() {

            // Find model by ID
            Optional<Model> result = modelRepository.findById(testModel.getId());

            // Verify result -> model exists and has correct brand association
            assertThat(result).isPresent();
            assertThat(result.get().getBrand()).isNotNull();
            assertThat(result.get().getBrand().getName()).isEqualTo("BMW");
        }

        @Test
        @DisplayName("findById returns empty when model does not exist")
        void findById_NonExistingModel_ReturnsEmpty() {

            // Find model by ID
            Optional<Model> result = modelRepository.findById(99999);

            // Verify result -> model does not exist
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("findAll returns all models")
        void findAll_MultipleModels_ReturnsAllModels() {

            // Create additional models
            Model model2 = TestDataFactory.createModel(testBrand, "5 Series");
            Model model3 = TestDataFactory.createModel(testBrand, "7 Series");

            entityManager.persist(model2);
            entityManager.persist(model3);

            entityManager.flush();

            // Find all models
            List<Model> result = modelRepository.findAll();

            // Verify result -> all models are returned
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Model::getName)
                    .containsExactlyInAnyOrder("3 Series", "5 Series", "7 Series");
        }

        @Test
        @DisplayName("models from different brands are separate")
        void findAll_ModelsFromDifferentBrands_ReturnsAll() {

            // Create additional brand and model
            Brand audi = TestDataFactory.createBrand("Audi");
            entityManager.persist(audi);

            Model audiModel = TestDataFactory.createModel(audi, "A4");
            entityManager.persist(audiModel);

            entityManager.flush();

            // Find all models
            List<Model> result = modelRepository.findAll();

            // Verify result -> models are associated with correct brands
            assertThat(result).hasSize(2);
            assertThat(result).extracting(m -> m.getBrand().getName())
                    .containsExactlyInAnyOrder("BMW", "Audi");
        }
    }


    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("save persists new model")
        void save_NewModel_PersistsModel() {

            // Create new model
            Model newModel = TestDataFactory.defaultModel(testBrand)
                    .name("X5")
                    .description("Luxury SUV")
                    .build();

            Model saved = modelRepository.save(newModel);

            entityManager.flush();

            // Verify result -> model is saved correctly
            assertThat(saved.getId()).isNotNull();

            Model found = entityManager.find(Model.class, saved.getId());

            assertThat(found.getName()).isEqualTo("X5");
        }

        @Test
        @DisplayName("save updates existing model")
        void save_ExistingModel_UpdatesModel() {

            // Update existing model
            testModel.setName("3 Series Updated");
            testModel.setDescription("Updated description");

            // Save updated model
            modelRepository.save(testModel);

            entityManager.flush();
            entityManager.clear();

            // Verify result -> model is updated correctly
            Model found = entityManager.find(Model.class, testModel.getId());

            assertThat(found.getName()).isEqualTo("3 Series Updated");
            assertThat(found.getDescription()).isEqualTo("Updated description");
        }
    }


    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("delete removes model")
        void delete_ExistingModel_RemovesModel() {

            // Delete existing model
            modelRepository.delete(testModel);

            entityManager.flush();

            // Verify result -> model is removed
            Model found = entityManager.find(Model.class, testModel.getId());

            assertThat(found).isNull();
        }
    }
}
