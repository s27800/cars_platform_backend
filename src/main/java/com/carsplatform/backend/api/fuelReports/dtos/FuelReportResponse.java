package com.carsplatform.backend.api.fuelReports.dtos;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelReportResponse {
    private UUID id;
    private BigDecimal fuelConsumption;
    private String comment;
    private LocalDateTime reportDate;
    private Boolean isApproved;
    private Long likesCount;
    private UsernameResponse usernameResponse;
}
