package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.admin.AdminFuelReportMapper;
import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuelReportService {
    private final FuelReportRepository fuelReportRepository;
    private final AverageFuelConsumptionMapper averageFuelConsumptionMapper;
    private final FuelReportMapper fuelReportMapper;
    private final CreateFuelReportMapper createFuelReportMapper;
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final AdminFuelReportMapper adminFuelReportMapper;

    @Transactional(readOnly = true)
    public AverageFuelConsumptionResponse getAverageFuelConsumptionForCar(UUID carId) {
        Optional<BigDecimal> avgConsumption = fuelReportRepository.findAverageFuelConsumptionForCarId(carId);

        return averageFuelConsumptionMapper.toDto(
                avgConsumption.orElse(null)
        );
    }

    @Transactional(readOnly = true)
    public Page<FuelReportResponse> getFuelReportsForCarId(UUID carId, Pageable pageable) {
        return fuelReportMapper.toDtoList(
                fuelReportRepository.findByCarIdAndIsApprovedTrue(carId, pageable)
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminFuelReportResponse> getFuelReportsForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return adminFuelReportMapper.toDtoList(
                fuelReportRepository.findAllByUserId(user.getId(), pageable)
        );
    }

    @Transactional
    public void createFuelReport(UUID carId, CreateFuelReportRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        FuelReport report = createFuelReportMapper.toDto(request);

        report.setCar(car);
        report.setUser(user);

        fuelReportRepository.save(report);
    }
}
