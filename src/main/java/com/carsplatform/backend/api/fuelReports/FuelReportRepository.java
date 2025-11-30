package com.carsplatform.backend.api.fuelReports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FuelReportRepository extends JpaRepository<FuelReport, Long> {
    List<FuelReport> findByCarIdAndIsApproved(Integer carId, boolean isApproved);

    Page<FuelReport> findByCarIdAndIsApproved(Integer carId, boolean isApproved, Pageable pageable);

    @Query("SELECT AVG(fr.fuelConsumption) " +
            "FROM FuelReport fr " +
            "WHERE fr.car.id = :carId AND fr.isApproved = true")
    Optional<BigDecimal> findAverageFuelConsumptionForCarId(@Param("carId") Integer carId);
}
