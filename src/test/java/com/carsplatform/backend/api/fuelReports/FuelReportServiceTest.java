package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.TestDataFactory;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("FuelReportService Tests")
class FuelReportServiceTest {

    @Mock
    private FuelReportRepository fuelReportRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AverageFuelConsumptionMapper averageFuelConsumptionMapper;

    @Mock
    private FuelReportMapper fuelReportMapper;

    @Mock
    private CreateFuelReportMapper createFuelReportMapper;

    @InjectMocks
    private FuelReportService fuelReportService;

    private User testUser;
    private Car testCar;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(1L)
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(1)
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(1)
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(1)
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(1)
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(1)
                .build();
    }


    @Nested
    @DisplayName("getAverageFuelConsumptionForCar")
    class GetAverageFuelConsumptionForCarTests {

        @Test
        @DisplayName("should return average fuel consumption when reports exist")
        void getAverageFuelConsumptionForCar_ReportsExist_ReturnsAverage() {

            // Set up average fuel consumption
            BigDecimal avgConsumption = new BigDecimal("7.5");

            AverageFuelConsumptionResponse expectedResponse = AverageFuelConsumptionResponse.builder()
                    .averageFuelConsumption(avgConsumption)
                    .build();

            // Mock repository and mapper
            when(fuelReportRepository.findAverageFuelConsumptionForCarId(1))
                    .thenReturn(Optional.of(avgConsumption));
            when(averageFuelConsumptionMapper.toDto(avgConsumption)).thenReturn(expectedResponse);

            // Get average fuel consumption for car
            AverageFuelConsumptionResponse result = fuelReportService.getAverageFuelConsumptionForCar(1);

            // Verify result -> average fuel consumption is returned
            assertThat(result).isNotNull();
            assertThat(result.getAverageFuelConsumption()).isEqualTo(avgConsumption);

            verify(fuelReportRepository).findAverageFuelConsumptionForCarId(1);
            verify(averageFuelConsumptionMapper).toDto(avgConsumption);
        }

        @Test
        @DisplayName("should return zero when no reports exist")
        void getAverageFuelConsumptionForCar_NoReports_ReturnsZero() {

            // Set up expected response
            AverageFuelConsumptionResponse expectedResponse = AverageFuelConsumptionResponse.builder()
                    .averageFuelConsumption(BigDecimal.ZERO)
                    .build();

            // Mock repository and mapper
            when(fuelReportRepository.findAverageFuelConsumptionForCarId(1))
                    .thenReturn(Optional.empty());
            when(averageFuelConsumptionMapper.toDto(BigDecimal.ZERO)).thenReturn(expectedResponse);

            // Get average fuel consumption for car
            AverageFuelConsumptionResponse result = fuelReportService.getAverageFuelConsumptionForCar(1);

            // Verify result -> average fuel consumption is zero
            assertThat(result.getAverageFuelConsumption()).isEqualTo(BigDecimal.ZERO);
        }
    }


    @Nested
    @DisplayName("getFuelReportsForCarId")
    class GetFuelReportsForCarIdTests {

        @Test
        @DisplayName("should return paginated fuel reports for car")
        void getFuelReportsForCarId_ReportsExist_ReturnsPage() {

            // Set up fuel reports
            Pageable pageable = PageRequest.of(0, 10);
            FuelReport report = TestDataFactory.defaultFuelReport(testUser, testCar).id(1L).build();
            Page<FuelReport> reportPage = new PageImpl<>(List.of(report), pageable, 1);
            Page<FuelReportResponse> expectedResponse = new PageImpl<>(
                    List.of(FuelReportResponse.builder().id(1L).build()),
                    pageable, 1
            );

            // Mock repository and mapper
            when(fuelReportRepository.findByCarIdAndIsApprovedTrue(1, pageable)).thenReturn(reportPage);
            when(fuelReportMapper.toDtoList(reportPage)).thenReturn(expectedResponse);

            // Get fuel reports for car
            Page<FuelReportResponse> result = fuelReportService.getFuelReportsForCarId(1, pageable);

            // Verify result -> paginated fuel reports are returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(fuelReportRepository).findByCarIdAndIsApprovedTrue(1, pageable);
            verify(fuelReportMapper).toDtoList(reportPage);
        }
    }


    @Nested
    @DisplayName("createFuelReport")
    class CreateFuelReportTests {

        @Test
        @DisplayName("should create fuel report when user and car exist")
        void createFuelReport_ValidData_CreatesReport() {

            // Set up request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .comment("City driving")
                    .build();

            FuelReport mappedReport = new FuelReport();

            // Mock repository and mapper
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(carRepository.findById(1)).thenReturn(Optional.of(testCar));
            when(createFuelReportMapper.toDto(request)).thenReturn(mappedReport);

            // Create fuel report
            fuelReportService.createFuelReport(1, request, "testuser");

            // Verify result -> fuel report is saved
            ArgumentCaptor<FuelReport> reportCaptor = ArgumentCaptor.forClass(FuelReport.class);

            verify(fuelReportRepository).save(reportCaptor.capture());

            FuelReport savedReport = reportCaptor.getValue();

            assertThat(savedReport.getCar()).isEqualTo(testCar);
            assertThat(savedReport.getUser()).isEqualTo(testUser);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user not found")
        void createFuelReport_UserNotFound_ThrowsException() {

            // Set up request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .build();

            // Mock repository
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

            // Create fuel report and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> fuelReportService.createFuelReport(1, request, "unknown"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(fuelReportRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when car not found")
        void createFuelReport_CarNotFound_ThrowsException() {

            // Set up request
            CreateFuelReportRequest request = CreateFuelReportRequest.builder()
                    .fuelConsumption(new BigDecimal("8.5"))
                    .build();

            // Mock repository
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
            when(carRepository.findById(999)).thenReturn(Optional.empty());

            // Create fuel report and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> fuelReportService.createFuelReport(999, request, "testuser"))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(fuelReportRepository, never()).save(any());
        }
    }
}
