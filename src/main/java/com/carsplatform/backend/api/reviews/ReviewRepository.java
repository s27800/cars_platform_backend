package com.carsplatform.backend.api.reviews;

import com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;


public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query(
        value =
            "SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "WHERE r.car.id = :carId AND r.status = com.carsplatform.backend.common.ModerationStatus.APPROVED",
        countQuery =
            "SELECT count(r) FROM Review r " +
            "WHERE r.car.id = :carId AND r.status = com.carsplatform.backend.common.ModerationStatus.APPROVED"
    )
    Page<Review> findAllApprovedByCarId(@Param("carId") UUID carId, Pageable pageable);

    @Query(
        value =
            "SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.car c " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "WHERE r.status = com.carsplatform.backend.common.ModerationStatus.PENDING",
        countQuery =
            "SELECT count(r) FROM Review r " +
            "WHERE r.status = com.carsplatform.backend.common.ModerationStatus.PENDING"
    )
    Page<Review> findAllPending(Pageable pageable);

    @Query(
        "SELECT new com.carsplatform.backend.api.reviews.dtos.AverageRatingsResponse(" +
        "AVG(r.engineRating), AVG(r.transmissionRating), AVG(r.steeringRating), " +
        "AVG(r.suspensionRating), AVG(r.visibilityRating), AVG(r.ergonomicsRating), " +
        "AVG(r.soundProofingRating), AVG(r.interiorSpaceRating), AVG(r.maintenanceRating), " +
        "AVG(r.priceQualityRating), AVG(r.failureFreeRating)) " +
        "FROM Review r WHERE r.car.id = :carId AND r.status = com.carsplatform.backend.common.ModerationStatus.APPROVED"
    )
    AverageRatingsResponse findAverageRatingsForCarId(@Param("carId") UUID carId);

    boolean existsByCarIdAndUserId(UUID carId, UUID userId);

    @Query(
        value =
            "SELECT r FROM Review r " +
            "LEFT JOIN FETCH r.user " +
            "LEFT JOIN FETCH r.car c " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "WHERE r.user.id = :userId",
        countQuery =
            "SELECT count(r) FROM Review r " +
            "WHERE r.user.id = :userId"
    )
    Page<Review> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);
}
