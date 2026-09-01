package com.carsplatform.backend.api.fuelReportLikes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface FuelReportLikeRepository extends JpaRepository<FuelReportLike, UUID> {

    Optional<FuelReportLike> findByUserIdAndFuelReportId(UUID userId, UUID fuelReportId);

    boolean existsByUserIdAndFuelReportId(UUID userId, UUID fuelReportId);

    long countByFuelReportId(UUID fuelReportId);
}
