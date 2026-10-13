package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.Crop;
import com.kisanmitra.entity.CropRequirement;
import com.kisanmitra.entity.TemperatureRange;
import com.kisanmitra.repository.CropRequirementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private CropRequirementRepository cropRequirementRepository;

    private RestTemplate restTemplate;
    private MockRestServiceServer mockServer;
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        weatherService = new WeatherService(cropRequirementRepository, restTemplate);
    }

    @Test
    @DisplayName("getWeatherInfo should parse current weather and forecast successfully")
    void getWeatherInfo_Success() {
        String jsonResponse = "{\n" +
                "  \"current\": {\n" +
                "    \"temperature_2m\": 32.5,\n" +
                "    \"apparent_temperature\": 34.0,\n" +
                "    \"relative_humidity_2m\": 70,\n" +
                "    \"precipitation\": 0.0,\n" +
                "    \"wind_speed_10m\": 15.0,\n" +
                "    \"weather_code\": 1,\n" +
                "    \"time\": \"2026-10-09T01:00\"\n" +
                "  },\n" +
                "  \"daily\": {\n" +
                "    \"time\": [\"2026-10-09\", \"2026-10-10\", \"2026-10-11\"],\n" +
                "    \"temperature_2m_min\": [22.0, 23.0, 21.0],\n" +
                "    \"temperature_2m_max\": [34.0, 36.0, 32.0],\n" +
                "    \"precipitation_sum\": [0.0, 12.0, 35.0],\n" +
                "    \"precipitation_probability_max\": [10, 60, 85],\n" +
                "    \"weather_code\": [0, 61, 82]\n" +
                "  }\n" +
                "}";

        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        WeatherResponseDto response = weatherService.getWeatherInfo("Andhra Pradesh", "Guntur", null);

        assertNotNull(response);
        assertEquals("Andhra Pradesh", response.getLocation().getState());
        assertEquals("Guntur", response.getLocation().getDistrict());
        assertNotNull(response.getCurrent());
        assertEquals(32.5, response.getCurrent().getTemperature());
        assertEquals("Mainly Clear", response.getCurrent().getWeatherCondition());
        assertEquals(3, response.getForecast().size());

        // Check computed risks
        assertNotNull(response.getRisks());
        boolean hasHeavyRain = response.getRisks().stream().anyMatch(r -> "HEAVY_RAIN".equals(r.getRiskType()) && "HIGH".equals(r.getLevel()));
        assertTrue(hasHeavyRain, "Should flag HIGH heavy rain risk due to 35mm forecast rain and 85% prob");

        boolean hasHighTemp = response.getRisks().stream().anyMatch(r -> "HIGH_TEMP".equals(r.getRiskType()) && "MODERATE".equals(r.getLevel()));
        assertTrue(hasHighTemp, "Should flag MODERATE high temp risk due to 36°C max temp");

        assertNotNull(response.getDisclaimer());
        assertTrue(response.getDisclaimer().contains("decision-support indicators"));
    }

    @Test
    @DisplayName("getWeatherInfo should handle crop context when valid cropId is provided")
    void getWeatherInfo_WithCropContext() {
        String jsonResponse = "{\n" +
                "  \"current\": { \"temperature_2m\": 30.0, \"weather_code\": 0 },\n" +
                "  \"daily\": {\n" +
                "    \"time\": [\"2026-10-09\"],\n" +
                "    \"temperature_2m_min\": [20.0],\n" +
                "    \"temperature_2m_max\": [30.0],\n" +
                "    \"precipitation_sum\": [0.0],\n" +
                "    \"precipitation_probability_max\": [0],\n" +
                "    \"weather_code\": [0]\n" +
                "  }\n" +
                "}";

        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        Crop crop = new Crop("Rice", "Oryza sativa", "Paddy crop");
        crop.setId(1L);

        CropRequirement req = new CropRequirement();
        req.setCrop(crop);
        req.setMinTemperatureRange(TemperatureRange.WARM);
        req.setMaxTemperatureRange(TemperatureRange.HOT);
        req.setWaterRequirementDescription("High water requirement (1200 - 1500 mm)");
        req.setSourceName("ICAR Guidelines");

        when(cropRequirementRepository.findByCropId(1L)).thenReturn(Optional.of(req));

        WeatherResponseDto response = weatherService.getWeatherInfo("Andhra Pradesh", "Guntur", 1L);

        assertNotNull(response.getCropContext());
        assertTrue(response.getCropContext().getAvailable());
        assertEquals("Rice", response.getCropContext().getCropName());
        assertTrue(response.getCropContext().getSummary().contains("Rice"));
    }

    @Test
    @DisplayName("getWeatherInfo should return unavailable crop context when cropId requirement does not exist")
    void getWeatherInfo_CropNotFound() {
        String jsonResponse = "{\n" +
                "  \"current\": { \"temperature_2m\": 30.0, \"weather_code\": 0 },\n" +
                "  \"daily\": {\n" +
                "    \"time\": [\"2026-10-09\"],\n" +
                "    \"temperature_2m_min\": [20.0],\n" +
                "    \"temperature_2m_max\": [30.0],\n" +
                "    \"precipitation_sum\": [0.0],\n" +
                "    \"precipitation_probability_max\": [0],\n" +
                "    \"weather_code\": [0]\n" +
                "  }\n" +
                "}";

        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        when(cropRequirementRepository.findByCropId(999L)).thenReturn(Optional.empty());

        WeatherResponseDto response = weatherService.getWeatherInfo("Andhra Pradesh", "Guntur", 999L);

        assertNotNull(response.getCropContext());
        assertFalse(response.getCropContext().getAvailable());
        assertTrue(response.getCropContext().getSummary().contains("not available"));
    }

    @Test
    @DisplayName("getWeatherInfo should throw 503 Service Unavailable when weather API fails")
    void getWeatherInfo_ProviderFailure() {
        mockServer.expect(requestTo(org.hamcrest.Matchers.containsString("/forecast")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> weatherService.getWeatherInfo("Andhra Pradesh", "Guntur", null));

        assertEquals(503, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Weather service is currently unavailable"));
    }
}
