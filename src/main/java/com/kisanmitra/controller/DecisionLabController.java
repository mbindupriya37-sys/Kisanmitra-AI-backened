package com.kisanmitra.controller;

import com.kisanmitra.dto.DecisionLabRequestDto;
import com.kisanmitra.dto.DecisionLabResponseDto;
import com.kisanmitra.service.DecisionLabService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer/decision-lab")
public class DecisionLabController {

    private final DecisionLabService decisionLabService;

    public DecisionLabController(DecisionLabService decisionLabService) {
        this.decisionLabService = decisionLabService;
    }

    @PostMapping("/simulate")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<DecisionLabResponseDto> simulateWhatIfScenario(@Valid @RequestBody DecisionLabRequestDto dto) {
        DecisionLabResponseDto response = decisionLabService.simulate(dto);
        return ResponseEntity.ok(response);
    }
}
