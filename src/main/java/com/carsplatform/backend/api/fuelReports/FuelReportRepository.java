package com.carsplatform.backend.api.fuelReports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FuelReportRepository extends JpaRepository<FuelReport, UUID> {
    @EntityGraph(attributePaths = {"user"})
    Page<FuelReport> findByCarIdAndIsApprovedTrue(UUID carId, Pageable pageable);

    @Query(
            value =
                    "SELECT fr FROM FuelReport fr " +
                    "LEFT JOIN FETCH fr.user " +
                    "LEFT JOIN FETCH fr.car c " +
                    "LEFT JOIN FETCH c.generation g " +
                    "LEFT JOIN FETCH g.model m " +
                    "LEFT JOIN FETCH m.brand " +
                    "WHERE fr.isApproved = false",
            countQuery =
                    "SELECT count(fr) FROM FuelReport fr " +
                    "WHERE fr.isApproved = false")
    Page<FuelReport> findAllPending(Pageable pageable);

    @Query("SELECT AVG(fr.fuelConsumption) " +
            "FROM FuelReport fr " +
            "WHERE fr.car.id = :carId AND fr.isApproved = true")
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
                    "WHERE fr.user.id = :userId")
    Page<FuelReport> findAllByUserId(@Param("userId") UUID userId, Pageable pageable);
}
