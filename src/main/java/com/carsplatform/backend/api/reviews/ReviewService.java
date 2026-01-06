package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarRepository carRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public AverageRatingsResponse getAverageRatingsForCar(Integer carId) {
        if (!carRepository.existsById(carId))
            throw new ResourceNotFoundException("Car", "id", carId);

        return reviewRepository.findAverageRatingsForCarId(carId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForCarId(Integer carId, Pageable pageable) {
        return reviewMapper.toDtoList(
                reviewRepository.findAllApprovedByCarId(carId, pageable)
        );
    }
}
