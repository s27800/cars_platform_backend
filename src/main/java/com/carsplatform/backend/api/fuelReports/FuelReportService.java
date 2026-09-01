package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.fuelReports.dtos.AverageFuelConsumptionResponse;
import com.carsplatform.backend.api.fuelReports.dtos.CreateFuelReportRequest;
import com.carsplatform.backend.api.fuelReports.dtos.FuelReportDetailsResponse;
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


/**
 * Fuel consumption reported by users.
 *
 * The same user may add several reports for one car, for example for city and motorway
 * driving. Only approved reports are averaged, and with none of them the endpoint answers with
 * an empty body instead of a zero, so that the UI never shows an invented "0 l/100km".
 */
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
    private final FuelReportDetailsMapper fuelReportDetailsMapper;


    @Transactional(readOnly = true)
    public AverageFuelConsumptionResponse getAverageFuelConsumptionForCar(UUID carId) {
        Optional<BigDecimal> avgConsumption = fuelReportRepository.findAverageFuelConsumptionForCarId(carId);

        return averageFuelConsumptionMapper.toDto(avgConsumption.orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<FuelReportResponse> getFuelReportsForCarId(UUID carId, Pageable pageable) {
        return fuelReportMapper.toDtoList(fuelReportRepository.findAllApprovedByCarId(carId, pageable));
    }

    @Transactional(readOnly = true)
    public Page<FuelReportDetailsResponse> getFuelReportsForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return fuelReportDetailsMapper.toDtoList(fuelReportRepository.findAllByUserId(user.getId(), pageable));
    }

    @Transactional
    public void createFuelReport(UUID carId, CreateFuelReportRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        FuelReport report = createFuelReportMapper.toEntity(request);

        report.setCar(car);
        report.setUser(user);

        fuelReportRepository.save(report);
    }

    @Transactional
    public void deleteOwnFuelReport(UUID fuelReportId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        FuelReport report = fuelReportRepository.findById(fuelReportId)
                .orElseThrow(() -> new ResourceNotFoundException("FuelReport", "id", fuelReportId));

        if (!report.getUser().getId().equals(user.getId()))
            throw new IllegalStateException("You can only delete your own fuel reports.");

        fuelReportRepository.delete(report);
    }
}
