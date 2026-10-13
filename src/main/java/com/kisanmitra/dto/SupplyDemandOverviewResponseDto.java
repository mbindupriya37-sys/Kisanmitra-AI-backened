package com.kisanmitra.dto;

import java.time.LocalDateTime;

public class SupplyDemandOverviewResponseDto {

    private String queryCrop;
    private String queryState;
    private String queryDistrict;
    private String querySeason;
    private LocalDateTime timestamp;

    private HistoricalProductionDto historicalProduction;
    private MandiPriceObservationDto mandiPrices;
    private MspNotificationDto mspNotification;
    private BufferStockDto bufferStocks;
    private ProcurementObservationDto publicProcurement;
    private DemandStatusDto regionalDemandBalance;

    public SupplyDemandOverviewResponseDto() {
        this.timestamp = LocalDateTime.now();
        this.regionalDemandBalance = new DemandStatusDto();
    }

    // Getters and Setters
    public String getQueryCrop() { return queryCrop; }
    public void setQueryCrop(String queryCrop) { this.queryCrop = queryCrop; }

    public String getQueryState() { return queryState; }
    public void setQueryState(String queryState) { this.queryState = queryState; }

    public String getQueryDistrict() { return queryDistrict; }
    public void setQueryDistrict(String queryDistrict) { this.queryDistrict = queryDistrict; }

    public String getQuerySeason() { return querySeason; }
    public void setQuerySeason(String querySeason) { this.querySeason = querySeason; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public HistoricalProductionDto getHistoricalProduction() { return historicalProduction; }
    public void setHistoricalProduction(HistoricalProductionDto historicalProduction) { this.historicalProduction = historicalProduction; }

    public MandiPriceObservationDto getMandiPrices() { return mandiPrices; }
    public void setMandiPrices(MandiPriceObservationDto mandiPrices) { this.mandiPrices = mandiPrices; }

    public MspNotificationDto getMspNotification() { return mspNotification; }
    public void setMspNotification(MspNotificationDto mspNotification) { this.mspNotification = mspNotification; }

    public BufferStockDto getBufferStocks() { return bufferStocks; }
    public void setBufferStocks(BufferStockDto bufferStocks) { this.bufferStocks = bufferStocks; }

    public ProcurementObservationDto getPublicProcurement() { return publicProcurement; }
    public void setPublicProcurement(ProcurementObservationDto publicProcurement) { this.publicProcurement = publicProcurement; }

    public DemandStatusDto getRegionalDemandBalance() { return regionalDemandBalance; }
    public void setRegionalDemandBalance(DemandStatusDto regionalDemandBalance) { this.regionalDemandBalance = regionalDemandBalance; }
}
