package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.cars.ICarRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final IReviewRepository reviewRepository;
    public final ICarRepository carRepository;
    public final IAverageRatingsMapper averageRatingsMapper;

    @Transactional(readOnly = true)
    public AverageRatingsResponse getAverageRatingsForCar(Integer carId) {
        if (!carRepository.existsById(carId))
            throw new ResourceNotFoundException("Car", "id", carId);

        return averageRatingsMapper.toDto(
                reviewRepository.findAverageRatingsForCarId(carId));
    }
}
