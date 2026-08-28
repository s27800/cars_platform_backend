package com.carsplatform.backend.api.fuelReports;

import com.carsplatform.backend.api.cars.Car;
import com.carsplatform.backend.api.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fuel_report")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelReport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    @JsonIgnore
    private Car car;

    @Column(name = "fuel_consumption", nullable = false, precision = 3, scale = 1)
    @NotNull(message = "Fuel consumption cannot be null")
    @Digits(integer = 2, fraction = 1, message = "Invalid format")
    private BigDecimal fuelConsumption;

    @Column(name = "comm", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "report_date", updatable = false)
    private LocalDateTime reportDate;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Formula("(SELECT COUNT(*) FROM fuel_report_likes frl WHERE frl.fuel_report_id = id)")
    private Long likesCount;

    @PrePersist
    protected void onCreate() {
        reportDate = LocalDateTime.now();
    }
}
