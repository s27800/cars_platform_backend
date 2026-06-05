package com.carsplatform.backend.api.dataProposal.dtos;

import com.carsplatform.backend.api.admin.dtos.AdminCarInfoResponse;
import com.carsplatform.backend.api.dataProposal.DataProposalStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDataProposalsResponse {
    private Long id;
    private Long userId;
    private String username;
    private AdminCarInfoResponse carInfo;
    private String category;
    private String comment;
    private Map<String, Object> proposedValues;
    private DataProposalStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
