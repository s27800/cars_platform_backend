package com.carsplatform.backend.api.admin;

import com.carsplatform.backend.api.admin.dtos.AdminFuelReportResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/fuel-reports")
@RequiredArgsConstructor
@Tag(name = "Admin Fuel Reports", description = "Admin API for managing fuel reports moderation")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFuelReportController {

    private final AdminFuelReportService adminFuelReportService;

    @GetMapping("/pending")
    @Operation(summary = "Get all pending fuel reports waiting for approval")
    public ResponseEntity<Page<AdminFuelReportResponse>> getPendingFuelReports(Pageable pageable) {
        return ResponseEntity.ok(adminFuelReportService.getPendingFuelReports(pageable));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve or reject a fuel report")
    public ResponseEntity<Void> approveFuelReport(
            @Parameter(description = "ID of the fuel report") @PathVariable Long id,
            @Parameter(description = "Whether to approve (true) or reject (false) the fuel report")
            @RequestParam boolean approve) {

        adminFuelReportService.approveFuelReport(id, approve);

        return ResponseEntity.noContent().build();
    }
}
