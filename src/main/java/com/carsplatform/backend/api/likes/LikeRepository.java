package com.carsplatform.backend.api.likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByUserIdAndReviewId(UUID userId, UUID reviewId);

    boolean existsByUserIdAndReviewId(UUID userId, UUID reviewId);

    long countByReviewId(UUID reviewId);
}
