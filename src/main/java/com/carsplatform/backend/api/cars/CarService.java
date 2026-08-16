package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
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
    private final CarsListMapper carsListMapper;

    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(Integer id) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id).orElseThrow(
                () -> new ResourceNotFoundException("Car", "id", id));

        return carDetailsMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    public Page<CarsListResponse> searchCars(
            String search,
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
                search, brandIds, modelIds, generationIds, bodyTypeIds,
                tagIds, minDisplacement, maxDisplacement, engineTypes,
                minPower, maxPower, minTorque, maxTorque,
                drives, transmissionTypes, minSpeed, maxSpeed,
                minFuelConsumptionMixed, maxFuelConsumptionMixed, pageable);

        return carsListMapper.map(cars);
    }
}
