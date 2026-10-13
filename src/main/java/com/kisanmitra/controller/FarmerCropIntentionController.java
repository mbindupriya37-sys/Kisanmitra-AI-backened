package com.kisanmitra.controller;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.Season;
import com.kisanmitra.service.FarmerCropIntentionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer")
public class FarmerCropIntentionController {

    private final FarmerCropIntentionService farmerCropIntentionService;

    public FarmerCropIntentionController(FarmerCropIntentionService farmerCropIntentionService) {
        this.farmerCropIntentionService = farmerCropIntentionService;
    }

    @PostMapping("/crop-intentions")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerCropIntentionResponseDto> createIntention(@Valid @RequestBody FarmerCropIntentionRequestDto dto) {
        FarmerCropIntentionResponseDto response = farmerCropIntentionService.createIntention(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/crop-intentions")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<PageResponse<FarmerCropIntentionResponseDto>> getFarmerIntentions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        PageResponse<FarmerCropIntentionResponseDto> response = farmerCropIntentionService.getFarmerIntentions(page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/crop-intentions/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerCropIntentionResponseDto> updateIntention(
            @PathVariable Long id,
            @Valid @RequestBody FarmerCropIntentionRequestDto dto
    ) {
        FarmerCropIntentionResponseDto response = farmerCropIntentionService.updateIntention(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/crop-intentions/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<Void> deleteIntention(@PathVariable Long id) {
        farmerCropIntentionService.deleteIntention(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/oversupply-risk")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<OversupplyRiskResponseDto> getOversupplyRisk(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Season season
    ) {
        OversupplyRiskResponseDto response = farmerCropIntentionService.getOversupplyRisk(state, district, season);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/crops")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<CropSummaryDto>> getActiveCrops() {
        List<CropSummaryDto> crops = farmerCropIntentionService.getActiveCrops();
        return ResponseEntity.ok(crops);
    }
}
