package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.admin.AdminReviewMapper;
import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.cars.CarRepository;
import com.carsplatform.backend.api.reviews.dtos.CreateReviewRequest;
import com.carsplatform.backend.api.reviews.dtos.ReviewResponse;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceAlreadyExistsException;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;
import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CarRepository carRepository;
    private final ReviewMapper reviewMapper;
    private final CreateReviewMapper createReviewMapper;
    private final UserRepository userRepository;
    private final AdminReviewMapper adminReviewMapper;

    @Transactional(readOnly = true)
    public AverageRatingsResponse getAverageRatingsForCar(UUID carId) {
        if (!carRepository.existsById(carId))
            throw new ResourceNotFoundException("Car", "id", carId);

        return reviewRepository.findAverageRatingsForCarId(carId);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsForCarId(UUID carId, Pageable pageable) {
        return reviewMapper.toDtoList(
                reviewRepository.findAllApprovedByCarId(carId, pageable)
        );
    }

    @Transactional(readOnly = true)
    public Page<AdminReviewResponse> getReviewsForUser(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        return adminReviewMapper.toDtoList(
                reviewRepository.findAllByUserId(user.getId(), pageable)
        );
    }

    @Transactional
    public void createReview(UUID carId, CreateReviewRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car", "id", carId));

        if (reviewRepository.existsByCarIdAndUserId(carId, user.getId()))
            throw new ResourceAlreadyExistsException("username", username);

        Review review = createReviewMapper.toDto(request);
        review.setCar(car);
        review.setUser(user);

        reviewRepository.save(review);
    }

    @Transactional
    public void deleteOwnReview(UUID reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (!review.getUser().getId().equals(user.getId()))
            throw new IllegalStateException("You can only delete your own reviews.");

        reviewRepository.delete(review);
    }
}
