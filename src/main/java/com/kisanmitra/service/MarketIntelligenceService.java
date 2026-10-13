package com.kisanmitra.service;

import com.kisanmitra.dto.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MarketIntelligenceService {

    private static final String DISCLAIMER_TEXT = "Market prices are dated mandi observations reported by official agricultural channels. They reflect historical/current trading prices and are provided for decision-support only. They do not constitute price predictions, yield guarantees, or profit commitments.";
    private static final String SOURCE_ATTRIBUTION = "Data powered by AGMARKNET / Ministry of Agriculture, Govt. of India";

    private final MarketDataProvider marketDataProvider;

    public MarketIntelligenceService(MarketDataProvider marketDataProvider) {
        this.marketDataProvider = marketDataProvider;
    }

    public List<String> getSupportedCrops() {
        return marketDataProvider.getSupportedCrops();
    }

    public MarketResponseDto getMarketPrices(String crop, String state, String district, String market) {
        String effectiveCrop = (crop != null && !crop.trim().isEmpty()) ? crop.trim() : "Rice";
        String effectiveState = (state != null && !state.trim().isEmpty()) ? state.trim() : null;
        String effectiveDistrict = (district != null && !district.trim().isEmpty()) ? district.trim() : null;
        String effectiveMarket = (market != null && !market.trim().isEmpty()) ? market.trim() : null;

        List<MarketObservationDto> observations = marketDataProvider.fetchPrices(effectiveCrop, effectiveState, effectiveDistrict, effectiveMarket);
        List<MarketConsiderationDto> considerations = computeConsiderations(observations);

        return new MarketResponseDto(
                marketDataProvider.getSupportedCrops(),
                effectiveCrop,
                effectiveState,
                effectiveDistrict,
                effectiveMarket,
                observations,
                observations.size(),
                considerations,
                DISCLAIMER_TEXT,
                SOURCE_ATTRIBUTION,
                LocalDateTime.now()
        );
    }

    public MarketComparisonResponseDto compareMarkets(String crop, String state) {
        String effectiveCrop = (crop != null && !crop.trim().isEmpty()) ? crop.trim() : "Rice";
        String effectiveState = (state != null && !state.trim().isEmpty()) ? state.trim() : "Andhra Pradesh";

        List<MarketObservationDto> observations = marketDataProvider.fetchPrices(effectiveCrop, effectiveState, null, null);
        if (observations.isEmpty()) {
            return new MarketComparisonResponseDto(
                    effectiveCrop,
                    effectiveState,
                    0.0,
                    "₹/quintal",
                    Collections.emptyList(),
                    "No comparable market observations available for " + effectiveCrop + " in " + effectiveState + ".",
                    DISCLAIMER_TEXT
            );
        }

        double totalModal = observations.stream()
                .map(MarketObservationDto::getModalPrice)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        long count = observations.stream().filter(o -> o.getModalPrice() != null).count();
        double avgModal = count > 0 ? Math.round((totalModal / count) * 10.0) / 10.0 : 0.0;

        double maxModal = observations.stream()
                .map(MarketObservationDto::getModalPrice)
                .filter(Objects::nonNull)
                .max(Double::compareTo)
                .orElse(0.0);

        double minModal = observations.stream()
                .map(MarketObservationDto::getModalPrice)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(0.0);

        List<MarketComparisonItemDto> items = new ArrayList<>();
        for (MarketObservationDto obs : observations) {
            double price = obs.getModalPrice() != null ? obs.getModalPrice() : 0.0;
            double diff = Math.round((price - avgModal) * 10.0) / 10.0;
            boolean isHighest = price > 0 && Math.abs(price - maxModal) < 0.01;
            boolean isLowest = price > 0 && Math.abs(price - minModal) < 0.01;

            items.add(new MarketComparisonItemDto(
                    obs.getMarketName(),
                    obs.getDistrict(),
                    obs.getState(),
                    price,
                    obs.getUnit(),
                    obs.getObservationDate(),
                    diff,
                    isHighest,
                    isLowest,
                    obs.getFreshnessStatus()
            ));
        }

        String summary = String.format("Comparing %d mandi markets in %s for %s. Regional modal average is ₹%.1f/quintal. Prices range between ₹%.1f and ₹%.1f per quintal.",
                items.size(), effectiveState, effectiveCrop, avgModal, minModal, maxModal);

        return new MarketComparisonResponseDto(
                effectiveCrop,
                effectiveState,
                avgModal,
                "₹/quintal",
                items,
                summary,
                DISCLAIMER_TEXT
        );
    }

    public MarketTrendResponseDto getMarketTrends(String crop, String market) {
        String effectiveCrop = (crop != null && !crop.trim().isEmpty()) ? crop.trim() : "Rice";
        String effectiveMarket = (market != null && !market.trim().isEmpty()) ? market.trim() : "Guntur Mandi";

        List<MarketObservationDto> obsList = marketDataProvider.fetchPrices(effectiveCrop, null, null, effectiveMarket);

        List<MarketTrendPointDto> trendPoints = new ArrayList<>();
        if (obsList != null) {
            for (MarketObservationDto obs : obsList) {
                if (obs.getObservationDate() != null && obs.getModalPrice() != null) {
                    trendPoints.add(new MarketTrendPointDto(
                            obs.getObservationDate(),
                            obs.getModalPrice(),
                            obs.getMinPrice(),
                            obs.getMaxPrice(),
                            obs.getMarketName(),
                            obs.getUnit()
                    ));
                }
            }
        }

        boolean sufficient = trendPoints.size() >= 2;
        String direction = "INSUFFICIENT_DATA";
        if (sufficient) {
            double firstPrice = trendPoints.get(0).getModalPrice();
            double lastPrice = trendPoints.get(trendPoints.size() - 1).getModalPrice();
            if (lastPrice > firstPrice + 0.01) {
                direction = "RISING";
            } else if (lastPrice < firstPrice - 0.01) {
                direction = "FALLING";
            } else {
                direction = "STABLE";
            }
        }
        String explanation = sufficient
                ? String.format("Observed modal prices for %s at %s across %d dated historical observations.", effectiveCrop, effectiveMarket, trendPoints.size())
                : String.format("Insufficient dated historical observations returned by the market provider to construct a price trend chart for %s at %s.", effectiveCrop, effectiveMarket);

        String stateName = (!obsList.isEmpty() && obsList.get(0).getState() != null) ? obsList.get(0).getState() : "India";

        return new MarketTrendResponseDto(
                effectiveCrop,
                effectiveMarket,
                stateName,
                "₹/quintal",
                sufficient,
                direction,
                trendPoints,
                explanation
        );
    }

    private List<MarketConsiderationDto> computeConsiderations(List<MarketObservationDto> observations) {
        List<MarketConsiderationDto> list = new ArrayList<>();

        list.add(new MarketConsiderationDto(
                "TRANSPORT",
                "Transport & Transit Costs",
                "Higher modal prices in distant markets do not guarantee higher net profit. Always factor in loading, fuel, and transport costs.",
                "🚛"
        ));

        list.add(new MarketConsiderationDto(
                "QUALITY",
                "Produce Quality & Grading",
                "Mandi modal prices apply to FAQ (Fair Average Quality) grade produce. Moister or ungraded produce may receive lower bids.",
                "🌾"
        ));

        list.add(new MarketConsiderationDto(
                "COMMISSION",
                "APMC Fees & Commissions",
                "Check local APMC mandi cess, commission agent charges, and weighing fees prior to dispatch.",
                "⚖️"
        ));

        list.add(new MarketConsiderationDto(
                "DATE_FRESHNESS",
                "Date Freshness & Arrival Volumes",
                "Verify the exact arrival date of market records. High daily arrivals can influence local modal prices rapidly.",
                "📅"
        ));

        return list;
    }
}
