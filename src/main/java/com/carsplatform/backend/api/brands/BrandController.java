package com.carsplatform.backend.api.brands;

import com.carsplatform.backend.api.brands.dtos.BrandsListResponse;
import com.carsplatform.backend.api.brands.dtos.BrandDetailsResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "Brands", description = "API for managing car brands")
@SecurityRequirement(name = "bearerAuth")
public class BrandController {

    private final BrandService brandService;


    @GetMapping
    @Operation(summary = "Get a list of all car brands")
    public ResponseEntity<List<BrandsListResponse>> getAllBrands() {
        List<BrandsListResponse> brands = brandService.getAllBrands();

        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get brand details with a list of models")
    public ResponseEntity<BrandDetailsResponse> getModelsByBrandId(
            @Parameter(description = "ID of the brand") @PathVariable UUID id
    ) {
        BrandDetailsResponse models = brandService.getBrandDetailsById(id);

        return ResponseEntity.ok(models);
    }
}
