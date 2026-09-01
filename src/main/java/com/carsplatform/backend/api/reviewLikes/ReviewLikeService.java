package com.carsplatform.backend.api.reviewLikes;

import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.LikeResponse;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Likes on reviews. A single endpoint toggles them: liking an already liked review takes the
 * like back.
 */
@Service
@RequiredArgsConstructor
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;


    @Transactional
    public LikeResponse toggleLike(UUID reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        var existingLike = reviewLikeRepository.findByUserIdAndReviewId(user.getId(), reviewId);

        boolean isLikedNow;

        if (existingLike.isPresent()) {
            reviewLikeRepository.delete(existingLike.get());

            isLikedNow = false;
        } else {
            try {
                ReviewLike newLike = ReviewLike.builder()
                        .user(user)
                        .review(review)
                        .build();

                reviewLikeRepository.save(newLike);

                isLikedNow = true;
            } catch (DataIntegrityViolationException e) {
                // The unique constraint stopped a parallel insert, so the review is liked either way
                isLikedNow = true;
            }
        }

        long count = reviewLikeRepository.countByReviewId(reviewId);

        return LikeResponse.builder()
                .isLiked(isLikedNow)
                .likesCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(UUID reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isLiked = reviewLikeRepository.existsByUserIdAndReviewId(user.getId(), reviewId);
        long count = reviewLikeRepository.countByReviewId(reviewId);

        return LikeResponse.builder()
                .isLiked(isLiked)
                .likesCount(count)
                .build();
    }
}
