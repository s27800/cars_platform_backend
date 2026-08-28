package com.carsplatform.backend.api.fuelReportLikes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FuelReportLikeRepository extends JpaRepository<FuelReportLike, UUID> {
    Optional<FuelReportLike> findByUserIdAndFuelReportId(UUID userId, UUID fuelReportId);

    boolean existsByUserIdAndFuelReportId(UUID userId, UUID fuelReportId);

    long countByFuelReportId(UUID fuelReportId);
}
