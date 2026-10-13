package com.kisanmitra.controller;

import com.kisanmitra.dto.GovernmentSchemeDto;
import com.kisanmitra.dto.GovernmentSchemeRequestDto;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.service.GovernmentSchemeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schemes")
public class GovernmentSchemeController {

    private final GovernmentSchemeService schemeService;

    public GovernmentSchemeController(GovernmentSchemeService schemeService) {
        this.schemeService = schemeService;
    }

    @GetMapping
    public ResponseEntity<List<GovernmentSchemeDto>> searchSchemes(
            @RequestParam(required = false) SchemeCategory category,
            @RequestParam(required = false) String search) {
        List<GovernmentSchemeDto> schemes = schemeService.searchSchemes(category, search);
        return ResponseEntity.ok(schemes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GovernmentSchemeDto> getSchemeById(@PathVariable Long id) {
        GovernmentSchemeDto scheme = schemeService.getSchemeById(id);
        return ResponseEntity.ok(scheme);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GovernmentSchemeDto> createScheme(@Valid @RequestBody GovernmentSchemeRequestDto request) {
        GovernmentSchemeDto created = schemeService.createScheme(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GovernmentSchemeDto> updateScheme(
            @PathVariable Long id,
            @Valid @RequestBody GovernmentSchemeRequestDto request) {
        GovernmentSchemeDto updated = schemeService.updateScheme(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteScheme(@PathVariable Long id) {
        schemeService.deleteScheme(id);
        return ResponseEntity.noContent().build();
    }
}
