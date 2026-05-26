package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query(
            value =
                    "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.user " +
                    "WHERE r.car.id = :carId AND r.isApproved = true",
            countQuery =
                    "SELECT count(r) FROM Review r " +
                    "WHERE r.car.id = :carId AND r.isApproved = true")
    Page<Review> findAllApprovedByCarId(@Param("carId") Integer carId, Pageable pageable);

    @Query(
            value =
                    "SELECT r FROM Review r " +
                    "LEFT JOIN FETCH r.user " +
                    "LEFT JOIN FETCH r.car c " +
                    "LEFT JOIN FETCH c.generation g " +
                    "LEFT JOIN FETCH g.model m " +
                    "LEFT JOIN FETCH m.brand " +
                    "WHERE r.isApproved = false",
            countQuery =
                    "SELECT count(r) FROM Review r " +
                    "WHERE r.isApproved = false")
    Page<Review> findAllPending(Pageable pageable);

    @Query("SELECT new com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse(" +
            "AVG(r.engineRating), AVG(r.transmissionRating), AVG(r.steeringRating), " +
            "AVG(r.suspensionRating), AVG(r.visibilityRating), AVG(r.ergonomicsRating), " +
            "AVG(r.soundProofingRating), AVG(r.interiorSpaceRating), AVG(r.maintenanceRating), " +
            "AVG(r.priceQualityRating), AVG(r.failureFreeRating)) " +
            "FROM Review r WHERE r.car.id = :carId AND r.isApproved = true")
    AverageRatingsResponse findAverageRatingsForCarId(@Param("carId") Integer carId);

    boolean existsByCarIdAndUserId(Integer carId, Long userId);
}
