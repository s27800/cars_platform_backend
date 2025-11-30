package com.carsplatform.backend.api.reviews;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface IReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCarIdAndIsApproved(Integer carId, boolean isApproved);

    Page<Review> findByCarIdAndIsApproved(Integer carId, boolean isApproved, Pageable pageable);

    long countByCarIdAndIsApproved(Integer carId, boolean isApproved);

    @Query("SELECT " +
            "AVG(r.engineRating), AVG(r.transmissionRating), AVG(r.steeringRating), " +
            "AVG(r.suspensionRating), AVG(r.visibilityRating), AVG(r.ergonomicsRating), " +
            "AVG(r.soundProofingRating), AVG(r.interiorSpaceRating), AVG(r.maintenanceRating), " +
            "AVG(r.priceQualityRating), AVG(r.failureFreeRating) " +
            "FROM Review r WHERE r.car.id = :carId AND r.isApproved = true")
    Map<String, Double> findAverageRatingsForCarId(@Param("carId") Integer carId);
}
