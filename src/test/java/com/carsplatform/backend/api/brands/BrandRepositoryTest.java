package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.common.TestDataFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("BrandRepository Integration Tests")
class BrandRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BrandRepository brandRepository;

    private Brand testBrand;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .name("BMW")
                .country("Germany")
                .foundedYear(1916)
                .build();

        entityManager.persist(testBrand);

        // Save
        entityManager.flush();
    }


    @Nested
    @DisplayName("findById Tests")
    class FindByIdTests {

        @Test
        @DisplayName("returns brand when exists")
        void findById_ExistingBrand_ReturnsBrand() {

            // Find existing brand
            Optional<Brand> result = brandRepository.findById(testBrand.getId());

            // Verify result -> brand exists and fields are correct
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("BMW");
            assertThat(result.get().getCountry()).isEqualTo("Germany");
        }

        @Test
        @DisplayName("returns empty when brand does not exist")
        void findById_NonExistingBrand_ReturnsEmpty() {

            // Find non-existing brand
            Optional<Brand> result = brandRepository.findById(99999);

            // Verify result -> brand does not exist
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("findAll Tests")
    class FindAllTests {

        @Test
        @DisplayName("returns all brands")
        void findAll_MultipleBrands_ReturnsAllBrands() {

            // Create additional brands
            Brand audi = TestDataFactory.createBrand("Audi");
            Brand mercedes = TestDataFactory.createBrand("Mercedes-Benz");

            entityManager.persist(audi);
            entityManager.persist(mercedes);

            entityManager.flush();

            // Find all brands
            List<Brand> result = brandRepository.findAll();

            // Verify result -> all brands are returned
            assertThat(result).hasSize(3);
            assertThat(result).extracting(Brand::getName)
                    .containsExactlyInAnyOrder("BMW", "Audi", "Mercedes-Benz");
        }

        @Test
        @DisplayName("with pagination returns paginated results")
        void findAll_WithPagination_ReturnsPaginatedResults() {

            // Create additional brands
            for (int i = 0; i < 5; i++) {
                Brand brand = TestDataFactory.createBrand("Brand" + i);
                entityManager.persist(brand);
            }

            entityManager.flush();

            // Find paginated brands
            Page<Brand> result = brandRepository.findAll(PageRequest.of(0, 3));

            // Verify result -> paginated results are correct
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(6);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("with sorting returns sorted results")
        void findAll_WithSorting_ReturnsSortedResults() {

            // Create additional brands
            Brand audi = TestDataFactory.createBrand("Audi");
            Brand volkswagen = TestDataFactory.createBrand("Volkswagen");

            entityManager.persist(audi);
            entityManager.persist(volkswagen);

            entityManager.flush();

            // Find all brands sorted by name
            List<Brand> result = brandRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

            // Verify result -> brands are sorted by name
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getName()).isEqualTo("Audi");
            assertThat(result.get(1).getName()).isEqualTo("BMW");
            assertThat(result.get(2).getName()).isEqualTo("Volkswagen");
        }
    }


    @Nested
    @DisplayName("save Tests")
    class SaveTests {

        @Test
        @DisplayName("persists new brand")
        void save_NewBrand_PersistsBrand() {

            // Create new brand
            Brand newBrand = TestDataFactory.defaultBrand()
                    .name("Toyota")
                    .country("Japan")
                    .foundedYear(1937)
                    .build();

            // Save new brand
            Brand saved = brandRepository.save(newBrand);

            entityManager.flush();

            // Verify result -> new brand is saved with correct fields
            assertThat(saved.getId()).isNotNull();

            Brand found = entityManager.find(Brand.class, saved.getId());

            assertThat(found.getName()).isEqualTo("Toyota");
        }

        @Test
        @DisplayName("updates existing brand")
        void save_ExistingBrand_UpdatesBrand() {

            // Update fields of existing brand
            testBrand.setName("BMW AG");
            testBrand.setFoundedYear(1917);

            // Save updated brand
            brandRepository.save(testBrand);
            entityManager.flush();
            entityManager.clear();

            // Verify result -> existing brand is updated with correct fields
            Brand found = entityManager.find(Brand.class, testBrand.getId());

            assertThat(found.getName()).isEqualTo("BMW AG");
            assertThat(found.getFoundedYear()).isEqualTo(1917);
        }
    }


    @Nested
    @DisplayName("delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("removes brand")
        void delete_ExistingBrand_RemovesBrand() {

            // Delete existing brand
            brandRepository.delete(testBrand);
            entityManager.flush();

            // Verify result -> brand is removed
            Brand found = entityManager.find(Brand.class, testBrand.getId());

            assertThat(found).isNull();
        }
    }


    @Nested
    @DisplayName("count Tests")
    class CountTests {

        @Test
        @DisplayName("returns correct number")
        void count_MultipleBrands_ReturnsCorrectCount() {

            // Create additional brand
            Brand brand2 = TestDataFactory.createBrand("Audi");

            entityManager.persist(brand2);
            entityManager.flush();

            // Count brands
            long count = brandRepository.count();

            // Verify count is valid
            assertThat(count).isEqualTo(2);
        }
    }


    @Nested
    @DisplayName("existsById Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("returns true for existing brand")
        void existsById_ExistingBrand_ReturnsTrue() {

            // Check if brand exists by ID
            boolean exists = brandRepository.existsById(testBrand.getId());

            // Verify result -> brand exists
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("returns false for non-existing brand")
        void existsById_NonExistingBrand_ReturnsFalse() {

            // Check if brand exists by ID
            boolean exists = brandRepository.existsById(99999);

            // Verify result -> brand does not exist
            assertThat(exists).isFalse();
        }
    }
}
