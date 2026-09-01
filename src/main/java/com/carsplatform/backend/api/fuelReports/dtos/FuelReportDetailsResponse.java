package com.carsplatform.backend.api.fuelReports.dtos;

import com.carsplatform.backend.api.cars.dtos.CarInfoResponse;
import com.carsplatform.backend.api.users.dtos.UsernameResponse;
import com.carsplatform.backend.common.ModerationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelReportDetailsResponse {
    private UUID id;
    private BigDecimal fuelConsumption;
    private String comment;
    private LocalDateTime reportDate;
    private ModerationStatus status;
    private Long likesCount;
    private UsernameResponse usernameResponse;
    private CarInfoResponse carInfo;
}
