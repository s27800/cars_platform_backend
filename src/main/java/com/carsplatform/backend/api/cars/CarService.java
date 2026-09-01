package com.carsplatform.backend.api.cars;

import com.carsplatform.backend.api.cars.dtos.CarDetailsResponse;
import com.carsplatform.backend.api.cars.dtos.CarsListResponse;
import com.carsplatform.backend.api.tags.Tag;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Read-only access to the car catalogue.
 *
 * Every filter of {@link #searchCars} is optional; a null value simply means no condition on
 * that field, which lets the frontend build the query out of whatever the user picked.
 * {@link #findSimilarCars} looks for cars sharing the brand, the body type or a tag, and the
 * limit is already applied in SQL so only the ids that are really needed are fetched.
 */
@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final CarDetailsMapper carDetailsMapper;
    private final CarsListMapper carsListMapper;


    @Transactional(readOnly = true)
    public CarDetailsResponse getCarDetailsForCarId(UUID id) throws ResourceNotFoundException {
        Car car = carRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", id));

        return carDetailsMapper.toDto(car);
    }

    @Transactional(readOnly = true)
    public Page<CarsListResponse> searchCars(
            String search,
            List<UUID> brandIds,
            List<UUID> modelIds,
            List<UUID> generationIds,
            List<UUID> bodyTypeIds,
            List<UUID> tagIds,
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

    @Transactional(readOnly = true)
    public List<CarsListResponse> findSimilarCars(UUID carId, int limit) {
        Car car = carRepository.findByIdWithTagsAndRelations(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        UUID brandId = car.getGeneration().getModel().getBrand().getId();
        UUID bodyTypeId = car.getBodyType().getId();

        Set<UUID> tagIds = car.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());

        List<UUID> similarCarIds = carRepository.findSimilarCarIds(
                carId,
                tagIds.isEmpty() ? null : tagIds,
                brandId,
                bodyTypeId,
                PageRequest.of(0, limit)
        );

        if (similarCarIds.isEmpty())
            return List.of();

        List<Car> similarCars = carRepository.findByIdsWithRelations(similarCarIds);

        return carsListMapper.map(similarCars);
    }
}
