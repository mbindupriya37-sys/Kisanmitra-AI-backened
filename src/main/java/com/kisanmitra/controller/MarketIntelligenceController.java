package com.kisanmitra.controller;

import com.kisanmitra.dto.*;
import com.kisanmitra.service.MarketIntelligenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farmer/market")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MarketIntelligenceController {

    private final MarketIntelligenceService marketIntelligenceService;

    public MarketIntelligenceController(MarketIntelligenceService marketIntelligenceService) {
        this.marketIntelligenceService = marketIntelligenceService;
    }

    @GetMapping("/crops")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<List<String>> getSupportedCrops() {
        return ResponseEntity.ok(marketIntelligenceService.getSupportedCrops());
    }

    @GetMapping("/prices")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<MarketResponseDto> getMarketPrices(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String market) {

        MarketResponseDto response = marketIntelligenceService.getMarketPrices(crop, state, district, market);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/compare")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<MarketComparisonResponseDto> compareMarkets(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String state) {

        MarketComparisonResponseDto response = marketIntelligenceService.compareMarkets(crop, state);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trends")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<MarketTrendResponseDto> getMarketTrends(
            @RequestParam(required = false) String crop,
            @RequestParam(required = false) String market) {

        MarketTrendResponseDto response = marketIntelligenceService.getMarketTrends(crop, market);
        return ResponseEntity.ok(response);
    }
}
