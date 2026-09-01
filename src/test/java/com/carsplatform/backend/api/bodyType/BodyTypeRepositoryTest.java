package com.carsplatform.backend.api.bodyType;

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
@DisplayName("BodyTypeRepository Integration Tests")
class BodyTypeRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BodyTypeRepository bodyTypeRepository;

    private BodyType testBodyType;

    @BeforeEach
    void setUp() {
        testBodyType = TestDataFactory.defaultBodyType()
                .name("Sedan")
                .build();
        entityManager.persist(testBodyType);

        entityManager.flush();
    }


    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("returns body type when exists")
        void findById_ExistingBodyType_ReturnsBodyType() {
            Optional<BodyType> result = bodyTypeRepository.findById(testBodyType.getId());
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Sedan");
        }

        @Test
        @DisplayName("returns empty when body type does not exist")
        void findById_NonExistingBodyType_ReturnsEmpty() {
            Optional<BodyType> result = bodyTypeRepository.findById(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("returns all body types")
        void findAll_MultipleBodyTypes_ReturnsAllBodyTypes() {
            BodyType hatchback = TestDataFactory.createBodyType("Hatchback");
            BodyType suv = TestDataFactory.createBodyType("SUV");
            BodyType coupe = TestDataFactory.createBodyType("Coupe");

            entityManager.persist(hatchback);
            entityManager.persist(suv);
            entityManager.persist(coupe);

            entityManager.flush();

            List<BodyType> result = bodyTypeRepository.findAll();
            assertThat(result).hasSize(4);
            assertThat(result).extracting(BodyType::getName)
                    .containsExactlyInAnyOrder("Sedan", "Hatchback", "SUV", "Coupe");
        }
    }


    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("persists new body type")
        void save_NewBodyType_PersistsBodyType() {
            BodyType newBodyType = TestDataFactory.defaultBodyType()
                    .name("Kombi")
                    .build();

            BodyType saved = bodyTypeRepository.save(newBodyType);
            entityManager.flush();
            assertThat(saved.getId()).isNotNull();
            BodyType found = entityManager.find(BodyType.class, saved.getId());
            assertThat(found.getName()).isEqualTo("Kombi");
        }

        @Test
        @DisplayName("updates existing body type")
        void save_ExistingBodyType_UpdatesBodyType() {
            testBodyType.setName("Sedan Sport");

            bodyTypeRepository.save(testBodyType);

            entityManager.flush();
            entityManager.clear();
            BodyType found = entityManager.find(BodyType.class, testBodyType.getId());

            assertThat(found.getName()).isEqualTo("Sedan Sport");
        }
    }


    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("removes body type")
        void delete_ExistingBodyType_RemovesBodyType() {
            bodyTypeRepository.delete(testBodyType);
            entityManager.flush();
            BodyType found = entityManager.find(BodyType.class, testBodyType.getId());

            assertThat(found).isNull();
        }
    }


    @Nested
    @DisplayName("count Tests")
    class CountTests {

        @Test
        @DisplayName("returns correct number")
        void count_MultipleBodyTypes_ReturnsCorrectCount() {
            BodyType bodyType2 = TestDataFactory.createBodyType("Hatchback");

            entityManager.persist(bodyType2);
            entityManager.flush();

            long count = bodyTypeRepository.count();
            assertThat(count).isEqualTo(2);
        }
    }


    @Nested
    @DisplayName("existsById Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("returns true for existing body type")
        void existsById_ExistingBodyType_ReturnsTrue() {
            boolean exists = bodyTypeRepository.existsById(testBodyType.getId());
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("returns false for non-existing body type")
        void existsById_NonExistingBodyType_ReturnsFalse() {
            boolean exists = bodyTypeRepository.existsById(UUID.randomUUID());
            assertThat(exists).isFalse();
        }
    }
}
