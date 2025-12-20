package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.fuelReports.FuelReport;
import com.carsplatform.backend.api.fuelReports.IFuelReportRepository;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.IReviewRepository;
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
    private final ICarRepository carRepository;
    private final ICarDetailsMapper carDetailsMapper;
    private final ICarsListMapper carsListMapper;
    private final IReviewRepository reviewRepository;
    private final IFuelReportRepository fuelReportRepository;

    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(Integer id) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id));

        List<Review> approvedReviews = reviewRepository.findByCarIdAndIsApproved(id, true);
        List<FuelReport> approvedReports = fuelReportRepository.findByCarIdAndIsApproved(id, true);

        car.setReviews(approvedReviews);
        car.setFuelReports(approvedReports);

        return carDetailsMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(Integer id, Pageable pageable) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id));

        Page<Review> approvedReviews = reviewRepository.findByCarIdAndIsApproved(id, true, pageable);
        Page<FuelReport> approvedReports = fuelReportRepository.findByCarIdAndIsApproved(id, true, pageable);

        car.setReviews(approvedReviews.stream().toList());
        car.setFuelReports(approvedReports.stream().toList());

        return carDetailsMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    public Page<CarsListResponse> searchCars(
            List<Integer> brandIds,
            List<Integer> modelIds,
            List<Integer> generationIds,
            List<Integer> bodyTypeIds,
            List<Integer> tagIds,
            Integer minDisplacement,
            Integer maxDisplacement,
            List<String> engineTypes,
            Integer minPower,
            Integer maxPower,
            Integer minTorque,
            Integer maxTorque,
            List<String> drives,
            List<String> transmissionTypes,
            Integer minSpeed,
            Integer maxSpeed,
            Double minFuelConsumptionMixed,
            Double maxFuelConsumptionMixed,
            Pageable pageable
    ) throws ResourceNotFoundException {

        Page<Car> cars = carRepository.searchCars(
                brandIds, modelIds, generationIds, bodyTypeIds,
                tagIds, minDisplacement, maxDisplacement, engineTypes,
                minPower, maxPower, minTorque, maxTorque,
                drives, transmissionTypes, minSpeed, maxSpeed,
                minFuelConsumptionMixed, maxFuelConsumptionMixed, pageable);

        return carsListMapper.map(cars);
    }
}
