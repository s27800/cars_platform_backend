package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.FuelReportRepository;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final CarDetailsMapper carDetailsMapper;
    private final ReviewRepository reviewRepository;
    private final FuelReportRepository fuelReportRepository;

    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(Integer id) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id));

        List<Review> approvedReviews = reviewRepository.findApprovedReviewsForCarId(id, true);
        List<FuelReport> approvedReports = fuelReportRepository.findApprovedFuelReportsForCarId(id, true);

        car.setReviews(approvedReviews);
        car.setFuelReports(approvedReports);

        return carDetailsMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(Integer id, Pageable pageable) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id));

        Page<Review> approvedReviews = reviewRepository.findApprovedReviewsForCarId(id, true, pageable);
        Page<FuelReport> approvedReports = fuelReportRepository.findApprovedFuelReportsForCarId(id, true, pageable);

        car.setReviews((List<Review>) approvedReviews);
        car.setFuelReports((List<FuelReport>) approvedReports);

        return carDetailsMapper.toDto(car);
    }
}
