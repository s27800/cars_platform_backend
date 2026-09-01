package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportDetailsMapper;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportDetailsResponse;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Moderation of fuel reports. A report only counts towards the average consumption
 * shown on the car page once it has been approved here.
 */
@Service
@RequiredArgsConstructor
public class AdminFuelReportService {

    private final FuelReportRepository fuelReportRepository;
    private final FuelReportDetailsMapper fuelReportDetailsMapper;


    @Transactional(readOnly = true)
    public Page<FuelReportDetailsResponse> getPendingFuelReports(Pageable pageable) {
        return fuelReportDetailsMapper.toDtoList(fuelReportRepository.findAllPending(pageable));
    }

    @Transactional
    public void approveFuelReport(UUID fuelReportId, boolean approve) {
        FuelReport fuelReport = fuelReportRepository.findById(fuelReportId)
                .orElseThrow(() -> new ResourceNotFoundException("FuelReport", "id", fuelReportId));

        if (approve)
            fuelReport.setStatus(ModerationStatus.APPROVED);
        else
            fuelReport.setStatus(ModerationStatus.REJECTED);

        fuelReportRepository.save(fuelReport);
    }
}
