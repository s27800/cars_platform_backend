package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.bodyType.BodyType;
import com.carsplatform.backend.api.brands.Brand;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportDetailsMapper;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportDetailsResponse;
import com.carsplatform.backend.api.generations.Generation;
import com.carsplatform.backend.api.models.Model;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.ModerationStatus;
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
    private FuelReportDetailsMapper fuelReportDetailsMapper;

    @InjectMocks
    private AdminFuelReportService adminFuelReportService;

    private User testUser;
    private Car testCar;
    private FuelReport testFuelReport;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.defaultUser()
                .id(UUID.randomUUID())
                .build();
        Brand brand = TestDataFactory.defaultBrand()
                .id(UUID.randomUUID())
                .build();
        Model model = TestDataFactory.defaultModel(brand)
                .id(UUID.randomUUID())
                .build();
        Generation generation = TestDataFactory.defaultGeneration(model)
                .id(UUID.randomUUID())
                .build();
        BodyType bodyType = TestDataFactory.defaultBodyType()
                .id(UUID.randomUUID())
                .build();
        testCar = TestDataFactory.defaultCar(generation, bodyType)
                .id(UUID.randomUUID())
                .build();
        testFuelReport = TestDataFactory.defaultFuelReport(testUser, testCar)
                .id(UUID.randomUUID())
                .status(ModerationStatus.PENDING)
                .build();
    }


    @Nested
    @DisplayName("getPendingFuelReports")
    class GetPendingFuelReportsTests {

        @Test
        @DisplayName("should return pending fuel reports page")
        void getPendingFuelReports_ReturnsPendingFuelReports() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<FuelReport> fuelReportPage = new PageImpl<>(List.of(testFuelReport));
            Page<FuelReportDetailsResponse> expectedResponse = new PageImpl<>(List.of(mock(FuelReportDetailsResponse.class)));

            when(fuelReportRepository.findAllPending(pageable)).thenReturn(fuelReportPage);
            when(fuelReportDetailsMapper.toDtoList(fuelReportPage)).thenReturn(expectedResponse);

            Page<FuelReportDetailsResponse> result = adminFuelReportService.getPendingFuelReports(pageable);
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);

            verify(fuelReportRepository).findAllPending(pageable);
            verify(fuelReportDetailsMapper).toDtoList(fuelReportPage);
        }

        @Test
        @DisplayName("should return empty page when no pending fuel reports")
        void getPendingFuelReports_NoPendingFuelReports_ReturnsEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<FuelReport> emptyPage = Page.empty();
            Page<FuelReportDetailsResponse> emptyResponse = Page.empty();

            when(fuelReportRepository.findAllPending(pageable)).thenReturn(emptyPage);
            when(fuelReportDetailsMapper.toDtoList(emptyPage)).thenReturn(emptyResponse);

            Page<FuelReportDetailsResponse> result = adminFuelReportService.getPendingFuelReports(pageable);
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
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));
            when(fuelReportRepository.save(any(FuelReport.class))).thenReturn(testFuelReport);

            adminFuelReportService.approveFuelReport(testFuelReport.getId(), true);
            ArgumentCaptor<FuelReport> fuelReportCaptor = ArgumentCaptor.forClass(FuelReport.class);

            verify(fuelReportRepository).save(fuelReportCaptor.capture());

            FuelReport savedFuelReport = fuelReportCaptor.getValue();

            assertThat(savedFuelReport.getStatus()).isEqualTo(ModerationStatus.APPROVED);
        }

        @Test
        @DisplayName("should set status to REJECTED when approve is false")
        void approveFuelReport_ApproveFalse_SetsStatusRejected() {
            when(fuelReportRepository.findById(testFuelReport.getId())).thenReturn(Optional.of(testFuelReport));

            adminFuelReportService.approveFuelReport(testFuelReport.getId(), false);
            ArgumentCaptor<FuelReport> fuelReportCaptor = ArgumentCaptor.forClass(FuelReport.class);
            verify(fuelReportRepository).save(fuelReportCaptor.capture());
            verify(fuelReportRepository, never()).delete(any());

            FuelReport savedFuelReport = fuelReportCaptor.getValue();
            assertThat(savedFuelReport.getStatus()).isEqualTo(ModerationStatus.REJECTED);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when fuel report not found")
        void approveFuelReport_FuelReportNotFound_ThrowsException() {
            UUID nonExistentId = UUID.randomUUID();

            when(fuelReportRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> adminFuelReportService.approveFuelReport(nonExistentId, true))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(fuelReportRepository, never()).save(any());
        }
    }
}
