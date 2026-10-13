package com.kisanmitra.service;

import com.kisanmitra.dto.MarketObservationDto;

import java.util.List;

public interface MarketDataProvider {

    List<MarketObservationDto> fetchPrices(String crop, String state, String district, String market);

    List<String> getSupportedCrops();
}
