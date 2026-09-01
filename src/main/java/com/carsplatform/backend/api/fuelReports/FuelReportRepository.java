package com.carsplatform.backend.api.fuelReports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


public interface FuelReportRepository extends JpaRepository<FuelReport, UUID> {
    @Query(
        value =
            "SELECT fr FROM FuelReport fr " +
            "LEFT JOIN FETCH fr.user " +
            "WHERE fr.car.id = :carId AND fr.status = com.carsplatform.backend.common.ModerationStatus.APPROVED",
        countQuery =
            "SELECT count(fr) FROM FuelReport fr " +
            "WHERE fr.car.id = :carId AND fr.status = com.carsplatform.backend.common.ModerationStatus.APPROVED"
    )
    Page<FuelReport> findAllApprovedByCarId(@Param("carId") UUID carId, Pageable pageable);

    @Query(
        value =
            "SELECT fr FROM FuelReport fr " +
            "LEFT JOIN FETCH fr.user " +
            "LEFT JOIN FETCH fr.car c " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "WHERE fr.status = com.carsplatform.backend.common.ModerationStatus.PENDING",
        countQuery =
            "SELECT count(fr) FROM FuelReport fr " +
            "WHERE fr.status = com.carsplatform.backend.common.ModerationStatus.PENDING"
    )
    Page<FuelReport> findAllPending(Pageable pageable);

    @Query(
        "SELECT AVG(fr.fuelConsumption) " +
        "FROM FuelReport fr " +
        "WHERE fr.car.id = :carId AND fr.status = com.carsplatform.backend.common.ModerationStatus.APPROVED"
    )
    Optional<BigDecimal> findAverageFuelConsumptionForCarId(@Param("carId") UUID carId);

    @Query(
        value =
            "SELECT fr FROM FuelReport fr " +
            "LEFT JOIN FETCH fr.user " +
            "LEFT JOIN FETCH fr.car c " +
            "LEFT JOIN FETCH c.generation g " +
            "LEFT JOIN FETCH g.model m " +
            "LEFT JOIN FETCH m.brand " +
            "WHERE fr.user.id = :userId",
        countQuery =
            "SELECT count(fr) FROM FuelReport fr " +
            "WHERE fr.user.id = :userId"
    )
    Page<FuelReport> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);
}
