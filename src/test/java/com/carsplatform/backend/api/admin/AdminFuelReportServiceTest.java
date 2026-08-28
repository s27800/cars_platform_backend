package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AdminFuelReportService Tests")
class AdminFuelReportServiceTest {

    @Mock
    private FuelReportRepository fuelReportRepository;

    @Mock
    private AdminFuelReportMapper adminFuelReportMapper;

    @InjectMocks
    private AdminFuelReportService adminFuelReportService;

    private User testUser;
    private Car testCar;
    private FuelReport testFuelReport;

    @BeforeEach
    void setUp() {

        // Create test user
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();

        // Create test brand
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();

        // Create test model
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();

        // Create test generation
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();

        // Create test body type
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();

        // Create test car
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();

        // Create test fuel report
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(UUID.randomUUID())
                .isApproved(false)
                .build();
    }


    @Nested
    @DisplayName("getPendingFuelReports")
    class GetPendingFuelReportsTests {

        @Test
        @DisplayName("should return pending fuel reports page")
        void getPendingFuelReports_ReturnsPendingFuelReports() {

            // Create pageable and mock data
            Pageable pageable = PageRequest.of(0, 10);
            Page<FuelReport> fuelReportPage = new PageImpl<>(List.of(testFuelReport));
            Page<AdminFuelReportResponse> expectedResponse = new PageImpl<>(List.of(mock(AdminFuelReportResponse.class)));

            // Mock repository and mapper
            when(fuelReportRepository.findAllPending(pageable)).thenReturn(fuelReportPage);
            when(adminFuelReportMapper.toDtoList(fuelReportPage)).thenReturn(expectedResponse);

            // Get pending fuel reports
            Page<AdminFuelReportResponse> result = adminFuelReportService.getPendingFuelReports(pageable);

            // Verify results -> pending fuel reports page is returned with correct content
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(fuelReportRepository).findAllPending(pageable);
            verify(adminFuelReportMapper).toDtoList(fuelReportPage);
        }

        @Test
        @DisplayName("should return empty page when no pending fuel reports")
        void getPendingFuelReports_NoPendingFuelReports_ReturnsEmptyPage() {

            // Create pageable and mock data
            Pageable pageable = PageRequest.of(0, 10);
            Page<FuelReport> emptyPage = Page.empty();
            Page<AdminFuelReportResponse> emptyResponse = Page.empty();

            // Mock repository and mapper
            when(fuelReportRepository.findAllPending(pageable)).thenReturn(emptyPage);
            when(adminFuelReportMapper.toDtoList(emptyPage)).thenReturn(emptyResponse);

            // Get pending fuel reports
            Page<AdminFuelReportResponse> result = adminFuelReportService.getPendingFuelReports(pageable);

            // Verify results -> empty page is returned
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();

            verify(fuelReportRepository).findAllPending(pageable);
        }
    }


    @Nested
    @DisplayName("approveFuelReport")
    class ApproveFuelReportTests {

        @Test
        @DisplayName("should approve fuel report when fuel report exists")
        void approveFuelReport_FuelReportExists_ApprovesFuelReport() {

            // Mock repository
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));
            when(fuelReportRepository.save(any(FuelReport.class))).thenReturn(testFuelReport);

            // Approve fuel report
            adminFuelReportService.approveFuelReport(testFuelReport.getId(), true);

            // Verify results -> fuel report is approved
            ArgumentCaptor<FuelReport> fuelReportCaptor = ArgumentCaptor.forClass(FuelReport.class);

            verify(fuelReportRepository).save(fuelReportCaptor.capture());

            FuelReport savedFuelReport = fuelReportCaptor.getValue();

            assertThat(savedFuelReport.getIsApproved()).isTrue();
        }

        @Test
        @DisplayName("should delete fuel report when approve is false")
        void approveFuelReport_ApproveFalse_DeletesFuelReport() {

            // Mock repository
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));
            doNothing().when(fuelReportRepository).delete(any(FuelReport.class));

            // Reject fuel report
            adminFuelReportService.approveFuelReport(testFuelReport.getId(), false);

            // Verify results -> fuel report is deleted
            verify(fuelReportRepository).delete(testFuelReport);
            verify(fuelReportRepository, never()).save(any());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when fuel report not found")
        void approveFuelReport_FuelReportNotFound_ThrowsException() {

            // Mock repository
            UUID nonExistentId = UUID.randomUUID();
            
            when(fuelReportRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // Approve fuel report and verify result -> ResourceNotFoundException is thrown
            assertThatThrownBy(() -> adminFuelReportService.approveFuelReport(nonExistentId, true))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(fuelReportRepository, never()).save(any());
        }
    }
}
