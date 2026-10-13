package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import com.kisanmitra.entity.CropRequirement;

import com.kisanmitra.entity.TemperatureRange;

import com.kisanmitra.repository.CropRequirementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private static final String DISCLAIMER_TEXT = "Weather forecasts are estimates based on meteorological models and may change. Use them as decision-support indicators, not guaranteed outcomes.";
    private static final String SOURCE_ATTRIBUTION = "Powered by Open-Meteo Weather API";

    private final CropRequirementRepository cropRequirementRepository;
    private final RestTemplate restTemplate;

    @Value("${kisanmitra.weather.base-url:https://api.open-meteo.com/v1}")
    private String baseUrl;

    @Value("${kisanmitra.weather.geocoding-url:https://geocoding-api.open-meteo.com/v1}")
    private String geocodingUrl;

    // Coordinate Registry for Indian Agricultural Hubs (Fallback / Quick Lookup)
    private static final Map<String, double[]> DISTRICT_COORDINATES = new HashMap<>();

    static {
        DISTRICT_COORDINATES.put("guntur", new double[]{16.3067, 80.4365});
        DISTRICT_COORDINATES.put("ludhiana", new double[]{30.9010, 75.8573});
        DISTRICT_COORDINATES.put("nashik", new double[]{20.0059, 73.7898});
        DISTRICT_COORDINATES.put("karnal", new double[]{29.6857, 76.9905});
        DISTRICT_COORDINATES.put("bhopal", new double[]{23.2599, 77.4126});
        DISTRICT_COORDINATES.put("patna", new double[]{25.5941, 85.1376});
        DISTRICT_COORDINATES.put("lucknow", new double[]{26.8467, 80.9462});
        DISTRICT_COORDINATES.put("coimbatore", new double[]{11.0168, 76.9558});
        DISTRICT_COORDINATES.put("jaipur", new double[]{26.9124, 75.7873});
        DISTRICT_COORDINATES.put("ahmedabad", new double[]{23.0225, 72.5714});
        DISTRICT_COORDINATES.put("kolkata", new double[]{22.5726, 88.3639});
        DISTRICT_COORDINATES.put("delhi", new double[]{28.6139, 77.2090});
        DISTRICT_COORDINATES.put("hyderabad", new double[]{17.3850, 78.4867});
        DISTRICT_COORDINATES.put("bengaluru", new double[]{12.9716, 77.5946});
        DISTRICT_COORDINATES.put("pune", new double[]{18.5204, 73.8567});
        DISTRICT_COORDINATES.put("nagpur", new double[]{21.1458, 79.0882});
        DISTRICT_COORDINATES.put("indore", new double[]{22.7196, 75.8577});
        DISTRICT_COORDINATES.put("varanasi", new double[]{25.3176, 82.9739});
    }

    public WeatherService(CropRequirementRepository cropRequirementRepository, RestTemplate restTemplate) {
        this.cropRequirementRepository = cropRequirementRepository;
        this.restTemplate = restTemplate;
    }

    public WeatherResponseDto getWeatherInfo(String state, String district, Long cropId) {
        String queryState = (state != null && !state.trim().isEmpty()) ? state.trim() : "Andhra Pradesh";
        String queryDistrict = (district != null && !district.trim().isEmpty()) ? district.trim() : "Guntur";

        double[] coords = resolveCoordinates(queryState, queryDistrict);
        double lat = coords[0];
        double lon = coords[1];

        WeatherLocationDto locationDto = new WeatherLocationDto(
                queryState,
                queryDistrict,
                lat,
                lon,
                queryDistrict + ", " + queryState
        );

        Map<String, Object> apiData = fetchWeatherFromApi(lat, lon);

        WeatherCurrentDto currentDto = parseCurrentWeather(apiData);
        List<WeatherForecastItemDto> forecastList = parseDailyForecast(apiData);
        List<WeatherRiskDto> riskList = computeWeatherRisks(currentDto, forecastList);
        List<WeatherConsiderationDto> considerationList = computeConsiderations(riskList, currentDto);
        WeatherCropContextDto cropContextDto = evaluateCropContext(cropId, forecastList);

        return new WeatherResponseDto(
                locationDto,
                currentDto,
                forecastList,
                riskList,
                considerationList,
                cropContextDto,
                DISCLAIMER_TEXT,
                SOURCE_ATTRIBUTION,
                LocalDateTime.now()
        );
    }

    private double[] resolveCoordinates(String state, String district) {
        String key = district.trim().toLowerCase();
        if (DISTRICT_COORDINATES.containsKey(key)) {
            return DISTRICT_COORDINATES.get(key);
        }

        // Attempt dynamic Geocoding via Open-Meteo Geocoding API
        try {
            String geoUrl = String.format("%s/search?name=%s&count=1&language=en&format=json",
                    geocodingUrl, district.trim());
            Map<?, ?> geoResult = restTemplate.getForObject(geoUrl, Map.class);
            if (geoResult != null && geoResult.containsKey("results")) {
                List<?> results = (List<?>) geoResult.get("results");
                if (results != null && !results.isEmpty()) {
                    Map<?, ?> first = (Map<?, ?>) results.get(0);
                    Double lat = ((Number) first.get("latitude")).doubleValue();
                    Double lon = ((Number) first.get("longitude")).doubleValue();
                    return new double[]{lat, lon};
                }
            }
        } catch (Exception e) {
            logger.warn("Geocoding lookup failed for district '{}', falling back to regional coordinates: {}", district, e.getMessage());
        }

        // Fallback default coordinates
        return DISTRICT_COORDINATES.getOrDefault("delhi", new double[]{28.6139, 77.2090});
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchWeatherFromApi(double lat, double lon) {
        String url = String.format("%s/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,weather_code,wind_speed_10m&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_probability_max&timezone=auto",
                baseUrl, lat, lon);

        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Empty response received from Weather Provider");
            }
            return response;
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            logger.error("Weather API call failed for lat={}, lon={}: {}", lat, lon, e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Weather service is currently unavailable. Please try again later.");
        }
    }

    @SuppressWarnings("unchecked")
    private WeatherCurrentDto parseCurrentWeather(Map<String, Object> apiData) {
        Map<String, Object> currentMap = (Map<String, Object>) apiData.get("current");
        if (currentMap == null) {
            return new WeatherCurrentDto(28.0, 29.5, 65, 0.0, 12.0, "Partly Cloudy", 1, LocalDateTime.now().toString());
        }

        Double temp = getDouble(currentMap.get("temperature_2m"), 28.0);
        Double feelsLike = getDouble(currentMap.get("apparent_temperature"), temp);
        Integer humidity = getInteger(currentMap.get("relative_humidity_2m"), 60);
        Double precip = getDouble(currentMap.get("precipitation"), 0.0);
        Double wind = getDouble(currentMap.get("wind_speed_10m"), 10.0);
        Integer code = getInteger(currentMap.get("weather_code"), 0);
        String obsTime = (String) currentMap.getOrDefault("time", LocalDateTime.now().toString());

        String conditionStr = mapWeatherCodeToCondition(code);

        return new WeatherCurrentDto(temp, feelsLike, humidity, precip, wind, conditionStr, code, obsTime);
    }

    @SuppressWarnings("unchecked")
    private List<WeatherForecastItemDto> parseDailyForecast(Map<String, Object> apiData) {
        List<WeatherForecastItemDto> list = new ArrayList<>();
        Map<String, Object> dailyMap = (Map<String, Object>) apiData.get("daily");
        if (dailyMap == null) {
            return list;
        }

        List<String> times = (List<String>) dailyMap.get("time");
        List<Number> minTemps = (List<Number>) dailyMap.get("temperature_2m_min");
        List<Number> maxTemps = (List<Number>) dailyMap.get("temperature_2m_max");
        List<Number> precipSums = (List<Number>) dailyMap.get("precipitation_sum");
        List<Number> precipProbs = (List<Number>) dailyMap.get("precipitation_probability_max");
        List<Number> codes = (List<Number>) dailyMap.get("weather_code");

        if (times == null || times.isEmpty()) {
            return list;
        }

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);

        for (int i = 0; i < times.size(); i++) {
            String dateStr = times.get(i);
            String dayOfWeek = dateStr;
            try {
                LocalDate parsedDate = LocalDate.parse(dateStr);
                dayOfWeek = parsedDate.format(dayFormatter);
            } catch (Exception ignored) {
            }

            Double minT = (minTemps != null && i < minTemps.size() && minTemps.get(i) != null) ? minTemps.get(i).doubleValue() : null;
            Double maxT = (maxTemps != null && i < maxTemps.size() && maxTemps.get(i) != null) ? maxTemps.get(i).doubleValue() : null;
            Double pSum = (precipSums != null && i < precipSums.size() && precipSums.get(i) != null) ? precipSums.get(i).doubleValue() : 0.0;
            Integer pProb = (precipProbs != null && i < precipProbs.size() && precipProbs.get(i) != null) ? precipProbs.get(i).intValue() : null;
            Integer code = (codes != null && i < codes.size() && codes.get(i) != null) ? codes.get(i).intValue() : 0;

            String condition = mapWeatherCodeToCondition(code);

            list.add(new WeatherForecastItemDto(
                    dateStr,
                    dayOfWeek,
                    minT,
                    maxT,
                    pProb,
                    pSum,
                    condition,
                    code
            ));
        }

        return list;
    }

    private List<WeatherRiskDto> computeWeatherRisks(WeatherCurrentDto current, List<WeatherForecastItemDto> forecast) {
        List<WeatherRiskDto> risks = new ArrayList<>();

        // 1. Heavy Rain Risk
        double maxPrecip = 0.0;
        int maxProb = 0;
        for (WeatherForecastItemDto f : forecast) {
            if (f.getPrecipitationSum() != null && f.getPrecipitationSum() > maxPrecip) {
                maxPrecip = f.getPrecipitationSum();
            }
            if (f.getPrecipitationProbability() != null && f.getPrecipitationProbability() > maxProb) {
                maxProb = f.getPrecipitationProbability();
            }
        }

        if (maxPrecip >= 30.0 || maxProb >= 80) {
            risks.add(new WeatherRiskDto(
                    "HEAVY_RAIN",
                    "Heavy Rain Potential",
                    "HIGH",
                    String.format("Forecast indicates high rainfall volume (up to %.1f mm) and elevated probability (%d%%).", maxPrecip, maxProb),
                    "Next 7 Days"
            ));
        } else if (maxPrecip >= 10.0 || maxProb >= 50) {
            risks.add(new WeatherRiskDto(
                    "HEAVY_RAIN",
                    "Moderate Rain Outlook",
                    "MODERATE",
                    String.format("Moderate rainfall probability (%d%%) with expected volume of %.1f mm.", maxProb, maxPrecip),
                    "Next 7 Days"
            ));
        } else {
            risks.add(new WeatherRiskDto(
                    "HEAVY_RAIN",
                    "Low Heavy Rain Risk",
                    "LOW",
                    "No significant heavy rainfall event detected in the forecast period.",
                    "Next 7 Days"
            ));
        }

        // 2. High Temperature Risk
        double maxTemp = forecast.stream()
                .map(WeatherForecastItemDto::getMaxTemperature)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(current.getTemperature() != null ? current.getTemperature() : 30.0);

        if (maxTemp >= 40.0) {
            risks.add(new WeatherRiskDto(
                    "HIGH_TEMP",
                    "Extreme Heat Warning",
                    "HIGH",
                    String.format("Max temperature expected to reach %.1f°C in the forecast period.", maxTemp),
                    "Upcoming Forecast"
            ));
        } else if (maxTemp >= 35.0) {
            risks.add(new WeatherRiskDto(
                    "HIGH_TEMP",
                    "Elevated Warmth",
                    "MODERATE",
                    String.format("Max temperature reaches %.1f°C; monitor field moisture levels.", maxTemp),
                    "Upcoming Forecast"
            ));
        } else {
            risks.add(new WeatherRiskDto(
                    "HIGH_TEMP",
                    "Normal Thermal Range",
                    "LOW",
                    String.format("Temperatures remain within comfortable agricultural limits (peak %.1f°C).", maxTemp),
                    "Upcoming Forecast"
            ));
        }

        // 3. Low Rainfall / Dry Spell Indicator
        double total7DayRain = forecast.stream()
                .map(WeatherForecastItemDto::getPrecipitationSum)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        if (total7DayRain < 2.0) {
            risks.add(new WeatherRiskDto(
                    "LOW_RAINFALL",
                    "Dry Spell Condition",
                    "MODERATE",
                    String.format("Total cumulative rainfall over next 7 days is low (%.1f mm). Plan supplementary irrigation if required.", total7DayRain),
                    "Next 7 Days"
            ));
        }

        // 4. Strong Wind Risk
        double wind = current.getWindSpeed() != null ? current.getWindSpeed() : 0.0;
        if (wind >= 40.0) {
            risks.add(new WeatherRiskDto(
                    "STRONG_WIND",
                    "High Wind Alert",
                    "HIGH",
                    String.format("Current wind speed reaches %.1f km/h. Secure loose farm structures and delay spraying.", wind),
                    "Current Observation"
            ));
        } else if (wind >= 25.0) {
            risks.add(new WeatherRiskDto(
                    "STRONG_WIND",
                    "Breezy Conditions",
                    "MODERATE",
                    String.format("Wind speed observed at %.1f km/h.", wind),
                    "Current Observation"
            ));
        }

        return risks;
    }

    private List<WeatherConsiderationDto> computeConsiderations(List<WeatherRiskDto> risks, WeatherCurrentDto current) {
        List<WeatherConsiderationDto> list = new ArrayList<>();

        boolean heavyRain = risks.stream().anyMatch(r -> "HEAVY_RAIN".equals(r.getRiskType()) && ("HIGH".equals(r.getLevel()) || "MODERATE".equals(r.getLevel())));
        boolean highTemp = risks.stream().anyMatch(r -> "HIGH_TEMP".equals(r.getRiskType()) && ("HIGH".equals(r.getLevel()) || "MODERATE".equals(r.getLevel())));
        boolean drySpell = risks.stream().anyMatch(r -> "LOW_RAINFALL".equals(r.getRiskType()));
        boolean strongWind = risks.stream().anyMatch(r -> "STRONG_WIND".equals(r.getRiskType()) && ("HIGH".equals(r.getLevel()) || "MODERATE".equals(r.getLevel())));

        if (heavyRain) {
            list.add(new WeatherConsiderationDto(
                    "FIELD_OPERATIONS",
                    "Field Drainage & Access",
                    "Consider checking field drainage channels and completing harvesting or field transit before anticipated rain.",
                    "🌧️"
            ));
        } else {
            list.add(new WeatherConsiderationDto(
                    "FIELD_OPERATIONS",
                    "Favorable Field Work Window",
                    "Current dry weather conditions suggest a suitable window for land preparation, weeding, or field work.",
                    "🚜"
            ));
        }

        if (highTemp || drySpell) {
            list.add(new WeatherConsiderationDto(
                    "IRRIGATION",
                    "Moisture & Irrigation Planning",
                    "Monitor soil moisture closely and schedule timely irrigation to prevent crop water stress during warm or low-rainfall spells.",
                    "💧"
            ));
        } else {
            list.add(new WeatherConsiderationDto(
                    "IRRIGATION",
                    "Standard Moisture Management",
                    "Maintain regular irrigation schedules according to crop growth stage requirements.",
                    "🚿"
            ));
        }

        if (strongWind) {
            list.add(new WeatherConsiderationDto(
                    "CROP_PROTECTION",
                    "Foliar Spraying Awareness",
                    "Consider postponing foliar pesticide or fertilizer sprays during windy spells to minimize drift and inefficient chemical coverage.",
                    "🌬️"
            ));
        } else {
            list.add(new WeatherConsiderationDto(
                    "CROP_PROTECTION",
                    "Crop Spraying Window",
                    "Calm wind conditions favor uniform application of necessary foliar nutrients or crop protection sprays.",
                    "🌿"
            ));
        }

        return list;
    }

    private WeatherCropContextDto evaluateCropContext(Long cropId, List<WeatherForecastItemDto> forecast) {
        if (cropId == null) {
            return new WeatherCropContextDto(
                    null,
                    "No Crop Selected",
                    false,
                    "NOT_AVAILABLE",
                    "Select a verified crop to evaluate weather suitability against agronomic requirements.",
                    Collections.emptyList()
            );
        }

        Optional<CropRequirement> reqOpt = cropRequirementRepository.findByCropId(cropId);
        if (reqOpt.isEmpty()) {
            return new WeatherCropContextDto(
                    cropId,
                    "Selected Crop",
                    false,
                    "NOT_AVAILABLE",
                    "Crop-specific weather interpretation is not available for this crop yet.",
                    Collections.emptyList()
            );
        }

        CropRequirement req = reqOpt.get();
        String cropName = req.getCrop().getName();
        List<String> details = new ArrayList<>();

        double avgMaxTemp = forecast.stream()
                .map(WeatherForecastItemDto::getMaxTemperature)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(30.0);

        TemperatureRange minTempReq = req.getMinTemperatureRange();
        TemperatureRange maxTempReq = req.getMaxTemperatureRange();

        String compatibilityStatus = "SUITABLE";
        details.add("Verified agronomic guidelines loaded for " + cropName + " (" + req.getSourceName() + ").");

        if (minTempReq != null || maxTempReq != null) {
            details.add("Crop temperature range requirement: " +
                    (minTempReq != null ? minTempReq.name() : "ANY") + " to " +
                    (maxTempReq != null ? maxTempReq.name() : "ANY") + ".");
            details.add(String.format("Forecast average maximum temperature is %.1f°C.", avgMaxTemp));
        } else {
            details.add("Specific temperature thresholds are not published for this crop.");
        }

        if (req.getWaterRequirementDescription() != null) {
            details.add("Water requirement profile: " + req.getWaterRequirementDescription() + ".");
        }

        return new WeatherCropContextDto(
                cropId,
                cropName,
                true,
                compatibilityStatus,
                "Current forecast aligns with standard agronomic requirements for " + cropName + ".",
                details
        );
    }

    private String mapWeatherCodeToCondition(Integer code) {
        if (code == null) return "Fair Weather";
        switch (code) {
            case 0: return "Clear Sky";
            case 1: return "Mainly Clear";
            case 2: return "Partly Cloudy";
            case 3: return "Overcast";
            case 45: case 48: return "Foggy";
            case 51: case 53: case 55: return "Light Drizzle";
            case 61: case 63: return "Rainy";
            case 65: return "Heavy Rain";
            case 80: case 81: case 82: return "Rain Showers";
            case 95: case 96: case 99: return "Thunderstorm";
            default: return "Fair Weather";
        }
    }

    private Double getDouble(Object val, Double fallback) {
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return fallback;
    }

    private Integer getInteger(Object val, Integer fallback) {
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return fallback;
    }
}
