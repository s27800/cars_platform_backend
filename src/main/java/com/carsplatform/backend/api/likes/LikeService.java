package com.carsplatform.backend.api.likes;

import com.carsplatform.backend.api.likes.dtos.LikeResponse;
import com.carsplatform.backend.api.reviews.ReviewRepository;
import com.carsplatform.backend.api.reviews.Review;
import com.carsplatform.backend.api.users.UserRepository;
import com.carsplatform.backend.api.users.User;
import com.carsplatform.backend.common.resourceExceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public LikeResponse toggleLike(UUID reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        var existingLike = likeRepository.findByUserIdAndReviewId(user.getId(), reviewId);

        boolean isLikedNow;

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            isLikedNow = false;
        } else {
            Like newLike = Like.builder()
                    .user(user)
                    .review(review)
                    .build();

            likeRepository.save(newLike);
            isLikedNow = true;
        }

        long count = likeRepository.countByReviewId(reviewId);

        return LikeResponse.builder()
                .isLiked(isLikedNow)
                .likesCount(count)
                .build();
    }

    @Transactional(readOnly = true)
    public LikeResponse getLikeStatus(UUID reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        boolean isLiked = likeRepository.existsByUserIdAndReviewId(user.getId(), reviewId);
        long count = likeRepository.countByReviewId(reviewId);

        return LikeResponse.builder()
                .isLiked(isLiked)
                .likesCount(count)
                .build();
    }
}
