package com.kisanmitra.controller;

import com.kisanmitra.dto.WeatherResponseDto;
import com.kisanmitra.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/farmer/weather")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<WeatherResponseDto> getCurrentWeather(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Long cropId) {

        WeatherResponseDto weather = weatherService.getWeatherInfo(state, district, cropId);
        return ResponseEntity.ok(weather);
    }

    @GetMapping("/forecast")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<WeatherResponseDto> getWeatherForecast(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Long cropId) {

        WeatherResponseDto weather = weatherService.getWeatherInfo(state, district, cropId);
        return ResponseEntity.ok(weather);
    }
}
