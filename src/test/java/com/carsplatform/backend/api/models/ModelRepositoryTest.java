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
import java.util.UUID;

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
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .build();

        entityManager.persist(testBrand);
        testModel = TestDataFactory.defaultModel(testBrand)
                .name("3 Series")
                .description("Compact executive car")
                .build();

        entityManager.persist(testModel);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("findById returns model when exists")
        void findById_ExistingModel_ReturnsModel() {
            Optional<Model> result = modelRepository.findById(testModel.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("3 Series");
        }

        @Test
        @DisplayName("model has correct brand association")
        void findById_ExistingModel_HasCorrectBrand() {
            Optional<Model> result = modelRepository.findById(testModel.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getBrand()).isNotNull();
            assertThat(result.get().getBrand().getName()).isEqualTo("BMW");
        }

        @Test
        @DisplayName("findById returns empty when model does not exist")
        void findById_NonExistingModel_ReturnsEmpty() {
            Optional<Model> result = modelRepository.findById(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("findAll returns all models")
        void findAll_MultipleModels_ReturnsAllModels() {
            Model model2 = TestDataFactory.createModel(testBrand, "5 Series");
            Model model3 = TestDataFactory.createModel(testBrand, "7 Series");

            entityManager.persist(model2);
            entityManager.persist(model3);

            entityManager.flush();

            List<Model> result = modelRepository.findAll();
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Model::getName)
                    .containsExactlyInAnyOrder("3 Series", "5 Series", "7 Series");
        }

        @Test
        @DisplayName("models from different brands are separate")
        void findAll_ModelsFromDifferentBrands_ReturnsAll() {
            Brand audi = TestDataFactory.createBrand("Audi");
            entityManager.persist(audi);

            Model audiModel = TestDataFactory.createModel(audi, "A4");
            entityManager.persist(audiModel);

            entityManager.flush();

            List<Model> result = modelRepository.findAll();
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
            Model newModel = TestDataFactory.defaultModel(testBrand)
                    .name("X5")
                    .description("Luxury SUV")
                    .build();

            Model saved = modelRepository.save(newModel);

            entityManager.flush();
            assertThat(saved.getId()).isNotNull();

            Model found = entityManager.find(Model.class, saved.getId());

            assertThat(found.getName()).isEqualTo("X5");
        }

        @Test
        @DisplayName("save updates existing model")
        void save_ExistingModel_UpdatesModel() {
            testModel.setName("3 Series Updated");
            testModel.setDescription("Updated description");

            modelRepository.save(testModel);

            entityManager.flush();
            entityManager.clear();
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
            modelRepository.delete(testModel);

            entityManager.flush();
            Model found = entityManager.find(Model.class, testModel.getId());

            assertThat(found).isNull();
        }
    }
}
