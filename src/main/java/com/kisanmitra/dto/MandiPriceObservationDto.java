package com.kisanmitra.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class MandiPriceObservationDto {

    private String availabilityStatus; // LIVE_CONFIRMED, UNCONFIRMED_OR_OFFLINE
    private String disclaimer;
    private List<MandiPriceRecord> observations;
    private ProvenanceMetaDto provenance;

    public MandiPriceObservationDto() {
    }

    public static class MandiPriceRecord {
        private String marketName;
        private String district;
        private String state;
        private String variety;
        private LocalDate arrivalDate;
        private BigDecimal minPriceInrPerQuintal;
        private BigDecimal maxPriceInrPerQuintal;
        private BigDecimal modalPriceInrPerQuintal;

        public MandiPriceRecord() {}

        public String getMarketName() { return marketName; }
        public void setMarketName(String marketName) { this.marketName = marketName; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        public String getVariety() { return variety; }
        public void setVariety(String variety) { this.variety = variety; }

        public LocalDate getArrivalDate() { return arrivalDate; }
        public void setArrivalDate(LocalDate arrivalDate) { this.arrivalDate = arrivalDate; }

        public BigDecimal getMinPriceInrPerQuintal() { return minPriceInrPerQuintal; }
        public void setMinPriceInrPerQuintal(BigDecimal minPriceInrPerQuintal) { this.minPriceInrPerQuintal = minPriceInrPerQuintal; }

        public BigDecimal getMaxPriceInrPerQuintal() { return maxPriceInrPerQuintal; }
        public void setMaxPriceInrPerQuintal(BigDecimal maxPriceInrPerQuintal) { this.maxPriceInrPerQuintal = maxPriceInrPerQuintal; }

        public BigDecimal getModalPriceInrPerQuintal() { return modalPriceInrPerQuintal; }
        public void setModalPriceInrPerQuintal(BigDecimal modalPriceInrPerQuintal) { this.modalPriceInrPerQuintal = modalPriceInrPerQuintal; }
    }

    // Getters and Setters
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }

    public List<MandiPriceRecord> getObservations() { return observations; }
    public void setObservations(List<MandiPriceRecord> observations) { this.observations = observations; }

    public ProvenanceMetaDto getProvenance() { return provenance; }
    public void setProvenance(ProvenanceMetaDto provenance) { this.provenance = provenance; }
}
