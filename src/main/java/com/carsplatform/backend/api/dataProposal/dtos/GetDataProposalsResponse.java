package com.carsplatform.backend.api.dataProposal.dtos;

import com.carsplatform.backend.api.cars.dtos.CarInfoResponse;
import com.carsplatform.backend.common.ModerationStatus;
import com.carsplatform.backend.common.ProposalCategory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDataProposalsResponse {
    private UUID id;
    private UUID userId;
    private String username;
    private CarInfoResponse carInfo;
    private ProposalCategory category;
    private String comment;
    private Map<String, Object> proposedValues;
    private ModerationStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
