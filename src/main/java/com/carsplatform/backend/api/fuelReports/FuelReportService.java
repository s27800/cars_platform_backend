package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelReportService {
    private final FuelReportRepository fuelReportRepository;
    private final AverageFuelConsumptionMapper averageFuelConsumptionMapper;

    @Transactional(readOnly = true)
    public AverageFuelConsumptionResponse getAverageFuelConsumptionForCar(Integer carId) {
        Optional<BigDecimal> avgConsumption = fuelReportRepository.findAverageFuelConsumptionForCarId(carId);

        return averageFuelConsumptionMapper.toDto(
                avgConsumption.orElse(BigDecimal.ZERO)
        );
    }
}
