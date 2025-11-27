package com.carsplatform.backend.api.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findApprovedReviewsForCarId(Integer carId, boolean isApproved);

    Page<Review> findApprovedReviewsForCarId(Integer carId, boolean isApproved, Pageable pageable);

    long countApprovedReviewsForCarId(Integer carId, boolean isApproved);

    @Query("SELECT AVG((" +
            "r.engineRating + r.transmissionRating + r.steeringRating " +
            "+ r.suspensionRating + r.visibilityRating + r.ergonomicsRating " +
            "+ r.soundProofingRating + r.interiorSpaceRating + r.maintenanceRating " +
            "+ r.priceQualityRating + r.failureFreeRating) / 11.0) " +
            "FROM Review r WHERE r.car.id = :carId AND r.isApproved = true")
    Double findAverageRatingForCarId(@Param("carId") Integer carId);
}
