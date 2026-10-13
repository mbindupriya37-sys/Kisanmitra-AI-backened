package com.kisanmitra.controller;

import com.kisanmitra.dto.CropRecommendationRequestDto;
import com.kisanmitra.dto.CropRecommendationResponseDto;
import com.kisanmitra.dto.CropRecommendationResultListDto;
import com.kisanmitra.dto.PageResponse;
import com.kisanmitra.service.CropRecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer/crop-recommendations")
public class CropRecommendationController {

    private final CropRecommendationService cropRecommendationService;

    public CropRecommendationController(CropRecommendationService cropRecommendationService) {
        this.cropRecommendationService = cropRecommendationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<CropRecommendationResponseDto> createRequest(@Valid @RequestBody CropRecommendationRequestDto dto) {
        CropRecommendationResponseDto response = cropRecommendationService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<PageResponse<CropRecommendationResponseDto>> getRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        PageResponse<CropRecommendationResponseDto> response = cropRecommendationService.getFarmerRequests(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{requestId}/results")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<CropRecommendationResultListDto> getRecommendationResults(@PathVariable Long requestId) {
        CropRecommendationResultListDto response = cropRecommendationService.getRecommendationResults(requestId);
        return ResponseEntity.ok(response);
    }
}
