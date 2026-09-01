package com.carsplatform.backend.api.reviewLikes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface ReviewLikeRepository extends JpaRepository<ReviewLike, UUID> {

    Optional<ReviewLike> findByUserIdAndReviewId(UUID userId, UUID reviewId);

    boolean existsByUserIdAndReviewId(UUID userId, UUID reviewId);

    long countByReviewId(UUID reviewId);
}
