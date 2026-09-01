package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.chassis.Chassis;
import com.carsplatform.backend.api.engines.Engine;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.insideDimensions.InsideDimensions;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.outsideDimensions.OutsideDimensions;
import com.carsplatform.backend.api.performances.Performance;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.api.transmissions.Transmission;
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CarRepository Integration Tests")
class CarRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    private Brand bmwBrand;
    private Brand audiBrand;
    private Model bmw3Series;
    private Model audiA4;
    private Generation bmwE90;
    private Generation audiB8;
    private BodyType sedan;
    private BodyType suv;
    private Tag sportTag;
    private Tag luxuryTag;

    @BeforeEach
    void setUp() {
        bmwBrand = TestDataFactory.createBrand("BMW");
        audiBrand = TestDataFactory.createBrand("Audi");

        entityManager.persist(bmwBrand);
        entityManager.persist(audiBrand);

        bmw3Series = TestDataFactory.createModel(bmwBrand, "3 Series");
        audiA4 = TestDataFactory.createModel(audiBrand, "A4");

        entityManager.persist(bmw3Series);
        entityManager.persist(audiA4);

        bmwE90 = TestDataFactory.createGeneration(bmw3Series, "E90");
        audiB8 = TestDataFactory.createGeneration(audiA4, "B8");

        entityManager.persist(bmwE90);
        entityManager.persist(audiB8);

        sedan = TestDataFactory.createBodyType("Sedan");
        suv = TestDataFactory.createBodyType("SUV");

        entityManager.persist(sedan);
        entityManager.persist(suv);

        sportTag = TestDataFactory.createTag("Sport");
        luxuryTag = TestDataFactory.createTag("Luxury");

        entityManager.persist(sportTag);
        entityManager.persist(luxuryTag);

        entityManager.flush();
    }

    private Car createCarWithDetails(
            Generation generation, BodyType bodyType, String name,
            String engineType, int maxPower, int displacement,
            String transmissionType, String drive, int maxSpeed,
            Double fuelConsumption
    ) {
        Engine engine = TestDataFactory.defaultEngine()
                .engineType(engineType)
                .maxPower(maxPower)
                .displacement(displacement)
                .maxTorque(300)
                .build();

        entityManager.persist(engine);
        Transmission transmission = TestDataFactory.defaultTransmission()
                .transmissionType(transmissionType)
                .build();

        entityManager.persist(transmission);
        Chassis chassis = TestDataFactory.defaultChassis()
                .drive(drive)
                .build();

        entityManager.persist(chassis);
        Performance performance = TestDataFactory.defaultPerformance()
                .maxSpeed(maxSpeed)
                .fuelConsumptionMixed(BigDecimal.valueOf(fuelConsumption))
                .build();

        entityManager.persist(performance);
        InsideDimensions inside = TestDataFactory.defaultInsideDimensions().build();
        entityManager.persist(inside);
        OutsideDimensions outside = TestDataFactory.defaultOutsideDimensions().build();
        entityManager.persist(outside);
        Car car = Car.builder()
                .name(name)
                .description("Test car")
                .doorsNumber(4)
                .seatsNumber(5)
                .productionYears("2020-2023")
                .generation(generation)
                .bodyType(bodyType)
                .engine(engine)
                .transmission(transmission)
                .chassis(chassis)
                .performance(performance)
                .insideDimensions(inside)
                .outsideDimensions(outside)
                .tags(new HashSet<>())
                .build();

        return car;
    }


    @Nested
    @DisplayName("findByIdWithDetails Tests")
    class FindByIdWithDetailsTests {

        @Test
        @DisplayName("returns car with all details when exists")
        void findByIdWithDetails_ExistingCar_ReturnsCarWithDetails() {
            Car car = createCarWithDetails(
                bmwE90, sedan, "320i", "Petrol", 184,
                1998, "Automatic", "RWD", 230, 7.5
                );

            entityManager.persist(car);
            entityManager.flush();
            entityManager.clear();

            Optional<Car> result = carRepository.findByIdWithDetails(car.getId());
            assertThat(result).isPresent();

            Car found = result.get();

            assertThat(found.getName()).isEqualTo("320i");
            assertThat(found.getEngine()).isNotNull();
            assertThat(found.getEngine().getMaxPower()).isEqualTo(184);
            assertThat(found.getTransmission()).isNotNull();
            assertThat(found.getChassis()).isNotNull();
            assertThat(found.getPerformance()).isNotNull();
            assertThat(found.getGeneration()).isNotNull();
            assertThat(found.getGeneration().getModel()).isNotNull();
            assertThat(found.getGeneration().getModel().getBrand()).isNotNull();
            assertThat(found.getGeneration().getModel().getBrand().getName()).isEqualTo("BMW");
        }

        @Test
        @DisplayName("returns empty when car does not exist")
        void findByIdWithDetails_NonExistingCar_ReturnsEmpty() {
            Optional<Car> result = carRepository.findByIdWithDetails(UUID.randomUUID());
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("searchCars Tests")
    class SearchCarsTests {

        private Car bmw320i;
        private Car bmw330i;
        private Car audiA4Sedan;

        @BeforeEach
        void setUpCars() {
            bmw320i = createCarWithDetails(
                bmwE90, sedan, "320i", "Petrol", 184,
                1998, "Manual", "RWD", 230, 7.5);

            bmw320i.setTags(Set.of(sportTag));
            entityManager.persist(bmw320i);

            bmw330i = createCarWithDetails(
                bmwE90, sedan, "330i", "Petrol", 258,
                2998, "Automatic", "RWD", 250, 8.5);

            bmw330i.setTags(Set.of(sportTag, luxuryTag));
            entityManager.persist(bmw330i);

            audiA4Sedan = createCarWithDetails(
                audiB8, sedan, "A4 2.0 TFSI", "Petrol", 211,
                1984, "Automatic", "AWD", 240, 7.8);

            audiA4Sedan.setTags(Set.of(luxuryTag));
            entityManager.persist(audiA4Sedan);

            entityManager.flush();
        }

        @Test
        @DisplayName("returns all cars when no filters applied")
        void searchCars_NoFilters_ReturnsAllCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("filters by brand IDs")
        void searchCars_ByBrandIds_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, List.of(bmwBrand.getId()), null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Car::getName)
                    .containsExactlyInAnyOrder("320i", "330i");
        }

        @Test
        @DisplayName("filters by model IDs")
        void searchCars_ByModelIds_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, List.of(audiA4.getId()), null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("A4 2.0 TFSI");
        }

        @Test
        @DisplayName("filters by generation IDs")
        void searchCars_ByGenerationIds_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, List.of(bmwE90.getId()), null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(2);
        }

        @Test
        @DisplayName("filters by body type IDs")
        void searchCars_ByBodyTypeIds_ReturnsMatchingCars() {
            Car bmwX5 = createCarWithDetails(bmwE90, suv, "X5", "Petrol", 306, 2998,
                    "Automatic", "AWD", 250, 10.5);

            entityManager.persist(bmwX5);
            entityManager.flush();

            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, List.of(suv.getId()), null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("X5");
        }

        @Test
        @DisplayName("filters by tag IDs")
        void searchCars_ByTagIds_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, List.of(luxuryTag.getId()),
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Car::getName)
                    .containsExactlyInAnyOrder("330i", "A4 2.0 TFSI");
        }

        @Test
        @DisplayName("filters by displacement range")
        void searchCars_ByDisplacementRange_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    2500, 3500, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("330i");
        }

        @Test
        @DisplayName("filters by engine types")
        void searchCars_ByEngineTypes_ReturnsMatchingCars() {
            Car diesel = createCarWithDetails(bmwE90, sedan, "320d", "Diesel", 177, 1995,
                    "Manual", "RWD", 225, 5.5);

            entityManager.persist(diesel);
            entityManager.flush();

            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, List.of("Diesel"), null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("320d");
        }

        @Test
        @DisplayName("filters by power range")
        void searchCars_ByPowerRange_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, 200, 300,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent()).extracting(Car::getName)
                    .containsExactlyInAnyOrder("330i", "A4 2.0 TFSI");
        }

        @Test
        @DisplayName("filters by drive types")
        void searchCars_ByDrives_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, List.of("AWD"), null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("A4 2.0 TFSI");
        }

        @Test
        @DisplayName("filters by transmission types")
        void searchCars_ByTransmissionTypes_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, List.of("Manual"), null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("filters by max speed range")
        void searchCars_ByMaxSpeedRange_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, 245,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("330i");
        }

        @Test
        @DisplayName("filters by fuel consumption range")
        void searchCars_ByFuelConsumptionRange_ReturnsMatchingCars() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, 7.6, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("320i");
        }

        @Test
        @DisplayName("applies multiple filters together")
        void searchCars_MultipleFilters_ReturnsIntersection() {
            Page<Car> result = carRepository.searchCars(
                    null, List.of(bmwBrand.getId()), null, null, null, null,
                    null, null, null, 200, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("330i");
        }

        @Test
        @DisplayName("returns paginated results")
        void searchCars_WithPagination_ReturnsPaginatedResults() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 2));
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(3);
            assertThat(result.getTotalPages()).isEqualTo(2);
        }

        @Test
        @DisplayName("returns empty when no cars match filters")
        void searchCars_NoMatches_ReturnsEmpty() {
            Page<Car> result = carRepository.searchCars(
                    null, null, null, null, null, null,
                    null, null, List.of("Electric"), null, null,
                    null, null, null, null, null,
                    null, null, null, PageRequest.of(0, 10));
            assertThat(result.getContent()).isEmpty();
        }
    }


    @Nested
    @DisplayName("Standard CRUD Tests")
    class CrudTests {

        @Test
        @DisplayName("save persists new car")
        void save_NewCar_PersistsCar() {
            Car car = createCarWithDetails(
                bmwE90, sedan, "318i", "Petrol", 143,
                1995, "Manual", "RWD", 210, 7.0);

            Car saved = carRepository.save(car);
            entityManager.flush();
            assertThat(saved.getId()).isNotNull();

            Car found = entityManager.find(Car.class, saved.getId());

            assertThat(found.getName()).isEqualTo("318i");
        }

        @Test
        @DisplayName("delete removes car")
        void delete_ExistingCar_RemovesCar() {
            Car car = createCarWithDetails(
                bmwE90, sedan, "Test", "Petrol", 100,
                1000, "Manual", "FWD", 180, 6.0);

            entityManager.persist(car);
            entityManager.flush();

            carRepository.delete(car);
            entityManager.flush();
            Car found = entityManager.find(Car.class, car.getId());
            assertThat(found).isNull();
        }

        @Test
        @DisplayName("findAll returns all cars")
        void findAll_MultipleCars_ReturnsAll() {
            Car car1 = createCarWithDetails(
                bmwE90, sedan, "A", "Petrol", 100,
                1000, "Manual", "FWD", 180, 6.0);

            Car car2 = createCarWithDetails(
                audiB8, sedan, "B", "Diesel", 150,
                2000, "Automatic", "AWD", 200, 5.5);

            entityManager.persist(car1);
            entityManager.persist(car2);
            entityManager.flush();

            List<Car> result = carRepository.findAll();
            assertThat(result).hasSize(2);
        }
    }
}
