package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelReportService {
    private final IFuelReportRepository fuelReportRepository;
    private final IAverageFuelConsumptionMapper averageFuelConsumptionMapper;
    private final IFuelReportMapper fuelReportMapper;

    @Transactional(readOnly = true)
    public AverageFuelConsumptionResponse getAverageFuelConsumptionForCar(Integer carId) {
        Optional<BigDecimal> avgConsumption = fuelReportRepository.findAverageFuelConsumptionForCarId(carId);

        return averageFuelConsumptionMapper.toDto(
                avgConsumption.orElse(BigDecimal.ZERO)
        );
    }

    @Transactional(readOnly = true)
    public Page<FuelReportResponse> getFuelReportsForCarId(Integer carId, Pageable pageable) {
        return fuelReportMapper.toDtoList(
                fuelReportRepository.findByCarIdAndIsApprovedTrue(carId, pageable)
        );
    }
}
