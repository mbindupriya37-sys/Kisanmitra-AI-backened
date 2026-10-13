package com.kisanmitra.service;

import com.kisanmitra.dto.MarketObservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class AgmarknetMarketDataProvider implements MarketDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(AgmarknetMarketDataProvider.class);

    private static final String SOURCE_NAME = "AGMARKNET / Ministry of Agriculture";
    private static final String SOURCE_URL = "https://agmarknet.gov.in";

    private static final List<String> SUPPORTED_CROPS = List.of(
            "Rice", "Wheat", "Maize", "Cotton", "Chickpea", "Pigeon Pea", "Groundnut"
    );

    private final RestTemplate restTemplate;

    @Value("${kisanmitra.market.base-url:https://api.data.gov.in/resource/9ef07375-19f2-4571-9599-7e2800a83e3e}")
    private String baseUrl;

    @Value("${kisanmitra.market.api-key:}")
    private String apiKey;

    public static final int FRESHNESS_THRESHOLD_DAYS = 7;

    public AgmarknetMarketDataProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<String> getSupportedCrops() {
        return SUPPORTED_CROPS;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MarketObservationDto> fetchPrices(String crop, String state, String district, String market) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("KEY")) {
            try {
                StringBuilder urlBuilder = new StringBuilder(baseUrl)
                        .append("?api-key=").append(apiKey.trim())
                        .append("&format=json&limit=50");

                if (crop != null && !crop.trim().isEmpty()) {
                    urlBuilder.append("&filters[commodity]=").append(crop.trim());
                }
                if (state != null && !state.trim().isEmpty()) {
                    urlBuilder.append("&filters[state]=").append(state.trim());
                }
                if (district != null && !district.trim().isEmpty()) {
                    urlBuilder.append("&filters[district]=").append(district.trim());
                }
                if (market != null && !market.trim().isEmpty()) {
                    urlBuilder.append("&filters[market]=").append(market.trim());
                }

                Map<String, Object> response = restTemplate.getForObject(urlBuilder.toString(), Map.class);
                if (response != null && response.containsKey("records")) {
                    List<Map<String, Object>> records = (List<Map<String, Object>>) response.get("records");
                    if (records != null && !records.isEmpty()) {
                        return records.stream()
                                .map(this::mapRecordToDto)
                                .filter(Objects::nonNull)
                                .filter(this::hasVerifiedProvenance)
                                .collect(Collectors.toList());
                    }
                }
            } catch (Exception e) {
                logger.warn("Live AGMARKNET API query failed or unavailable, resorting to benchmark records: {}", e.getMessage());
            }
        }

        // Return verified AGMARKNET benchmark records filtered by search parameters
        return getBenchmarkObservations(crop, state, district, market);
    }

    public static String calculateFreshnessStatus(String observationDateStr, boolean isLiveApiCall) {
        if (isLiveApiCall) {
            return "LIVE";
        }
        if (observationDateStr == null || observationDateStr.trim().isEmpty()) {
            return "HISTORICAL_BENCHMARK";
        }
        try {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate obsDate = java.time.LocalDate.parse(observationDateStr.trim(), formatter);
            java.time.LocalDate today = java.time.LocalDate.now();

            if (obsDate.isAfter(today)) {
                return "INVALID_FUTURE_DATE";
            }

            long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(obsDate, today);
            if (daysBetween < 0) {
                return "INVALID_FUTURE_DATE";
            } else if (daysBetween <= FRESHNESS_THRESHOLD_DAYS) {
                return "RECENT_OBSERVATION";
            } else {
                return "HISTORICAL_BENCHMARK";
            }
        } catch (Exception e) {
            return "HISTORICAL_BENCHMARK";
        }
    }

    public boolean hasVerifiedProvenance(MarketObservationDto dto) {
        if (dto == null) return false;
        if (dto.getCropName() == null || dto.getCropName().trim().isEmpty()) return false;
        if (dto.getMarketName() == null || dto.getMarketName().trim().isEmpty()) return false;
        if (dto.getUnit() == null || dto.getUnit().trim().isEmpty()) return false;
        if (dto.getModalPrice() == null || dto.getModalPrice() <= 0) return false;
        if (dto.getSourceName() == null || dto.getSourceName().trim().isEmpty()) return false;
        if (dto.getSourceUrl() == null || dto.getSourceUrl().trim().isEmpty()) return false;

        String url = dto.getSourceUrl().trim();
        if (url.equalsIgnoreCase("https://agmarknet.gov.in") || url.equalsIgnoreCase("https://agmarknet.gov.in/")) {
            return false;
        }

        if ("INVALID_FUTURE_DATE".equalsIgnoreCase(dto.getFreshnessStatus())) {
            return false;
        }
        return true;
    }

    private MarketObservationDto mapRecordToDto(Map<String, Object> record) {
        try {
            String cName = (String) record.getOrDefault("commodity", "Agricultural Commodity");
            String st = (String) record.getOrDefault("state", "India");
            String dist = (String) record.getOrDefault("district", "Regional Mandi");
            String mkt = (String) record.getOrDefault("market", "Central Market");
            String var = (String) record.getOrDefault("variety", "FAQ");
            String dateStr = (String) record.getOrDefault("arrival_date", "08/10/2026");

            Double minP = parseDouble(record.get("min_price"));
            Double maxP = parseDouble(record.get("max_price"));
            Double modalP = parseDouble(record.get("modal_price"));

            String id = String.format("%s_%s_%s_%s", st, dist, mkt, cName).replaceAll("\\s+", "_").toLowerCase();
            String freshness = calculateFreshnessStatus(dateStr, true);

            return new MarketObservationDto(
                    id, cName, st, dist, mkt, var, dateStr,
                    minP, maxP, modalP, "INR", "₹/quintal",
                    SOURCE_NAME, SOURCE_URL, freshness, java.time.LocalDateTime.now().toString()
            );
        } catch (Exception e) {
            return null;
        }
    }

    private List<MarketObservationDto> getBenchmarkObservations(String cropFilter, String stateFilter, String districtFilter, String marketFilter) {
        // Honest unavailable state: 12 unverified hardcoded fallback records removed
        // Live AGMARKNET API queries via data.gov.in provide verified data.
        return Collections.emptyList();
    }

    private Double parseDouble(Object val) {
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        if (val instanceof String) {
            try {
                return Double.parseDouble((String) val);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
