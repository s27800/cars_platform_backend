package com.carsplatform.backend.api.admin.dtos;

import com.carsplatform.backend.api.users.dtos.UsernameResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFuelReportResponse {
    private Long id;
    private BigDecimal fuelConsumption;
    private String comment;
    private LocalDateTime reportDate;
    private Boolean isApproved;
    private UsernameResponse usernameResponse;
    private AdminCarInfoResponse carInfo;
}
