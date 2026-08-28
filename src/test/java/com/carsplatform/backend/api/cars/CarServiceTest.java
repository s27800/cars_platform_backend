package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("CarService Tests")
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private CarDetailsMapper carDetailsMapper;

    @Mock
    private CarsListMapper carsListMapper;

    @InjectMocks
    private CarService carService;

    private Car testCar;
    private Brand testBrand;
    private Model testModel;
    private Generation testGeneration;
    private BodyType testBodyType;

    @BeforeEach
    void setUp() {

        // Create test brand
        testBrand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .name("BMW")
                .build();

        // Create test model
        testModel = TestDataFactory.defaultModel(testBrand)
                .id(UUID.randomUUID())
                .name("3 Series")
                .build();

        // Create test generation
        testGeneration = TestDataFactory.defaultGeneration(testModel)
                .id(UUID.randomUUID())
                .name("G20")
                .build();

        // Create test body type
        testBodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .name("Sedan")
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(testGeneration, testBodyType)
                .id(UUID.randomUUID())
                .name("BMW 330i")
                .build();
    }


    @Nested
    @DisplayName("getCarDetailsForCarId")
    class GetCarDetailsForCarIdTests {

        @Test
        @DisplayName("should return car details when car exists")
        void getCarDetailsForCarId_ExistingCar_ReturnsCarDetails() {

            // Create expected response DTO
            UUID testUuid = UUID.randomUUID();

            CarDetailsResponse expectedResponse = CarDetailsResponse.builder()
                    .id(testUuid)
                    .name("BMW 330i")
                    .doorsNumber(5)
                    .seatsNumber(5)
                    .productionYears("2018-2023")
                    .build();

            // Mock repository and mapper behavior
            when(carRepository.findByIdWithDetails(testCar.getId())).thenReturn(Optional.of(testCar));
            when(carDetailsMapper.toDto(testCar)).thenReturn(expectedResponse);

            // Get car details by ID
            CarDetailsResponse result = carService.getCarDetailsForCarId(testCar.getId());

            // Verify results -> correct car details are returned
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUuid);
            assertThat(result.getName()).isEqualTo("BMW 330i");

            verify(carRepository).findByIdWithDetails(testCar.getId());
            verify(carDetailsMapper).toDto(testCar);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when car does not exist")
        void getCarDetailsForCarId_NonExistingCar_ThrowsResourceNotFoundException() {

            // Mock repository to return empty optional
            UUID nonExistentId = UUID.randomUUID();
            
            when(carRepository.findByIdWithDetails(nonExistentId)).thenReturn(Optional.empty());

            // Get car details by ID and verify -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> carService.getCarDetailsForCarId(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(carRepository).findByIdWithDetails(nonExistentId);
            verify(carDetailsMapper, never()).toDto(any());
        }
    }


    @Nested
    @DisplayName("searchCars")
    class SearchCarsTests {

        @Test
        @DisplayName("should return filtered results when filters are provided")
        void searchCars_WithFilters_ReturnsFilteredResults() {

            // Create expected response DTOs and mock data
            Pageable pageable = PageRequest.of(0, 10);

            List<UUID> brandIds = List.of(testBrand.getId());

            Page<Car> carPage = new PageImpl<>(List.of(testCar), pageable, 1);
            Page<CarsListResponse> expectedResponse = new PageImpl<>(
                    List.of(CarsListResponse.builder()
                            .id(UUID.randomUUID())
                            .name("BMW 330i")
                            .build()),
                    pageable, 1
            );

            // Mock repository and mapper behavior
            when(carRepository.searchCars(
                    isNull(), eq(brandIds), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            )).thenReturn(carPage);

            when(carsListMapper.map(carPage)).thenReturn(expectedResponse);

            // Search cars with filters
            Page<CarsListResponse> result = carService.searchCars(
                    null, brandIds, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, pageable
            );

            // Verify results -> correct filtered cars are returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo("BMW 330i");

            verify(carRepository).searchCars(
                    isNull(), eq(brandIds), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            );
        }

        @Test
        @DisplayName("should return paginated results with default page")
        void searchCars_NoPagination_ReturnsDefaultPage() {

            // Create mock data
            Pageable pageable = PageRequest.of(0, 20);

            Page<Car> carPage = new PageImpl<>(List.of(testCar), pageable, 1);
            Page<CarsListResponse> expectedResponse = new PageImpl<>(
                    List.of(CarsListResponse.builder()
                            .id(UUID.randomUUID())
                            .name("BMW 330i")
                            .build()),
                    pageable, 1
            );

            // Mock repository and mapper behavior
            when(carRepository.searchCars(
                    isNull(), isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            )).thenReturn(carPage);

            when(carsListMapper.map(carPage)).thenReturn(expectedResponse);

            // Search cars without filters
            Page<CarsListResponse> result = carService.searchCars(
                    null, null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, pageable
            );

            // Verify results -> correct paginated results are returned
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(carsListMapper).map(carPage);
        }

        @Test
        @DisplayName("should return empty page when no cars match filters")
        void searchCars_NoMatches_ReturnsEmptyPage() {

            // Create mock data for non-existing brand
            Pageable pageable = PageRequest.of(0, 10);

            List<UUID> brandIds = List.of(UUID.randomUUID());

            Page<Car> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
            Page<CarsListResponse> expectedResponse = new PageImpl<>(
                    Collections.emptyList(), pageable, 0
            );

            // Mock repository and mapper behavior
            when(carRepository.searchCars(
                    isNull(), eq(brandIds), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            )).thenReturn(emptyPage);

            when(carsListMapper.map(emptyPage)).thenReturn(expectedResponse);

            // Search cars with non-matching filters
            Page<CarsListResponse> result = carService.searchCars(
                    null, brandIds, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, pageable
            );

            // Verify results -> empty page is returned
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("should apply multiple filters correctly")
        void searchCars_MultipleFilters_ReturnsIntersection() {

            // Create mock data with multiple filters
            Pageable pageable = PageRequest.of(0, 10);

            List<UUID> brandIds = List.of(testBrand.getId());
            List<UUID> bodyTypeIds = List.of(testBodyType.getId());

            Integer minPower = 200;
            Integer maxPower = 300;

            Page<Car> carPage = new PageImpl<>(List.of(testCar), pageable, 1);
            Page<CarsListResponse> expectedResponse = new PageImpl<>(
                    List.of(CarsListResponse.builder()
                            .id(UUID.randomUUID())
                            .name("BMW 330i")
                            .build()),
                    pageable, 1
            );

            // Mock repository and mapper behavior
            when(carRepository.searchCars(
                    isNull(), eq(brandIds), isNull(), isNull(), eq(bodyTypeIds),
                    isNull(), isNull(), isNull(), isNull(),
                    eq(minPower), eq(maxPower), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            )).thenReturn(carPage);

            when(carsListMapper.map(carPage)).thenReturn(expectedResponse);

            // Search cars with multiple filters
            Page<CarsListResponse> result = carService.searchCars(
                    null, brandIds, null, null, bodyTypeIds,
                    null, null, null, null,
                    minPower, maxPower, null, null,
                    null, null, null, null,
                    null, null, pageable
            );

            // Verify results -> correct filtered cars are returned
            assertThat(result.getContent()).hasSize(1);

            verify(carRepository).searchCars(
                    isNull(), eq(brandIds), isNull(), isNull(), eq(bodyTypeIds),
                    isNull(), isNull(), isNull(), isNull(),
                    eq(minPower), eq(maxPower), isNull(), isNull(),
                    isNull(), isNull(), isNull(), isNull(),
                    isNull(), isNull(), eq(pageable)
            );
        }
    }
}
