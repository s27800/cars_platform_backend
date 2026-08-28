package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminFuelReportService {

    private final FuelReportRepository fuelReportRepository;
    private final AdminFuelReportMapper adminFuelReportMapper;

    @Transactional(readOnly = true)
    public Page<AdminFuelReportResponse> getPendingFuelReports(Pageable pageable) {
        return adminFuelReportMapper.toDtoList(
                fuelReportRepository.findAllPending(pageable)
        );
    }

    @Transactional
    public void approveFuelReport(UUID fuelReportId, boolean approve) {
        FuelReport fuelReport = fuelReportRepository.findById(fuelReportId)
                .orElseThrow(() -> new ResourceNotFoundException("FuelReport", "id", fuelReportId));

        if (approve) {
            fuelReport.setIsApproved(true);
            fuelReportRepository.save(fuelReport);
        } else {
            fuelReportRepository.delete(fuelReport);
        }
    }
}
