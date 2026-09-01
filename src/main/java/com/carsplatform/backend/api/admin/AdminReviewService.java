package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewDetailsMapper;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.reviews.dtos.ReviewDetailsResponse;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Moderation of reviews. A review stays invisible to other users and out of the average
 * ratings until an admin approves it here.
 */
@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewDetailsMapper reviewDetailsMapper;


    @Transactional(readOnly = true)
    public Page<ReviewDetailsResponse> getPendingReviews(Pageable pageable) {
        return reviewDetailsMapper.toDtoList(reviewRepository.findAllPending(pageable));
    }

    @Transactional
    public void approveReview(UUID reviewId, boolean approve) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        if (approve)
            review.setStatus(ModerationStatus.APPROVED);
        else
            review.setStatus(ModerationStatus.REJECTED);

        reviewRepository.save(review);
    }
}
