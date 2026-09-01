package com.carsplatform.backend.api.dataProposal;

import com.carsplatform.backend.api.dataProposal.dtos.CreateDataProposalRequest;
import com.carsplatform.backend.api.dataProposal.dtos.GetDataProposalsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/data-proposals")
@RequiredArgsConstructor
@Tag(name = "Data Proposals", description = "API for data proposals")
@SecurityRequirement(name = "bearerAuth")
public class DataProposalController {

    private final DataProposalService dataProposalService;


    @PostMapping("/{carId}")
    @Operation(summary = "Create new data change proposal")
    public ResponseEntity<Void> createDataProposal(
            @PathVariable UUID carId,
            @RequestBody @Valid CreateDataProposalRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        dataProposalService.createDataProposal(carId, userDetails.getUsername(), request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get pending data proposals waiting to be resolved")
    public ResponseEntity<Page<GetDataProposalsResponse>> getPendingProposals(Pageable pageable) {
        return ResponseEntity.ok(dataProposalService.getPendingDataProposals(pageable));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resolve pending data proposal")
    public ResponseEntity<Void> resolveProposal(
            @PathVariable UUID id,
            @RequestParam boolean approve,
            @RequestParam(required = false) String adminComment
    ) {
        dataProposalService.resolveDataProposal(id, approve, adminComment);

        return ResponseEntity.noContent().build();
    }
}
