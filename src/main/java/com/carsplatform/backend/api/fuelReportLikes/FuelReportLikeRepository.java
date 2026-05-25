package com.carsplatform.backend.api.fuelReportLikes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuelReportLikeRepository extends JpaRepository<FuelReportLike, Long> {
    Optional<FuelReportLike> findByUserIdAndFuelReportId(Long userId, Long fuelReportId);

    boolean existsByUserIdAndFuelReportId(Long userId, Long fuelReportId);

    long countByFuelReportId(Long fuelReportId);
}
