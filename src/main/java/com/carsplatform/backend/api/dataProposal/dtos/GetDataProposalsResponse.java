package com.carsplatform.backend.api.dataProposal.dtos;

import com.carsplatform.backend.api.dataProposal.DataProposalStatus;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class GetDataProposalsResponse {
    private Long id;
    private String carName;
    private String category;
    private String comment;
    private Map<String, Object> proposedValues;
    private DataProposalStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
}
