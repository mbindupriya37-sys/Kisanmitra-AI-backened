package com.kisanmitra.controller;

import com.kisanmitra.dto.SupplyDemandOverviewResponseDto;
import com.kisanmitra.service.CropSupplyDemandService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/farmer/supply-demand")
public class CropSupplyDemandController {

    private final CropSupplyDemandService supplyDemandService;

    public CropSupplyDemandController(CropSupplyDemandService supplyDemandService) {
        this.supplyDemandService = supplyDemandService;
    }

    @GetMapping("/overview")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<SupplyDemandOverviewResponseDto> getOverview(
            @RequestParam(required = false, defaultValue = "Rice") String crop,
            @RequestParam(required = false, defaultValue = "Andhra Pradesh") String state,
            @RequestParam(required = false, defaultValue = "Krishna") String district,
            @RequestParam(required = false, defaultValue = "Kharif") String season) {
        SupplyDemandOverviewResponseDto response = supplyDemandService.getSupplyDemandOverview(crop, state, district, season);
        return ResponseEntity.ok(response);
    }
}
