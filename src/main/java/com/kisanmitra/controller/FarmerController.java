package com.kisanmitra.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<Map<String, String>> getFarmerDashboard() {
        return ResponseEntity.ok(Collections.singletonMap("message", "Farmer dashboard access granted"));
    }
}
