package com.carsplatform.backend.api.dataProposal.dtos;

import com.carsplatform.backend.common.ProposalCategory;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

import java.util.Map;


@Data
public class CreateDataProposalRequest {

    @NotNull
    private ProposalCategory category;

    private String comment;

    @NotEmpty
    private Map<String, Object> proposedValues;
}
