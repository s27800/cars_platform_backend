package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminReviewResponse;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;
    private final AdminReviewMapper adminReviewMapper;

    @Transactional(readOnly = true)
    public Page<AdminReviewResponse> getPendingReviews(Pageable pageable) {
        return adminReviewMapper.toDtoList(
                reviewRepository.findAllPending(pageable)
        );
    }

    @Transactional
    public void approveReview(Long reviewId, boolean approve) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        review.setIsApproved(approve);
        reviewRepository.save(review);
    }
}
