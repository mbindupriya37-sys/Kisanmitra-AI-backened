package com.kisanmitra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DemandStatusDto {

    private final String demandStatus = "INSUFFICIENT_DATA";
    private final String methodologyVersion = "IN_DEMAND_V1";

    @JsonProperty("isCalculated")
    private final boolean isCalculated = false;

    private final Double surplusDeficitRatio = null;
    private final Double surplusDeficitQuantity = null;
    private final String rationale = "Official Indian agricultural statistics report production supply at the district level (DES/UPAg), but official consumption demand is survey-based and published only at national and broad state levels (NSSO HCES / CACP). Localized district-level consumer demand data does not exist in official Indian statistics; computing a synthetic balance ratio is econometrically invalid.";
    private final String officialCitations = "MoSPI NSSO Household Consumption Expenditure Survey (HCES); CACP Reports on Price Policy for Kharif & Rabi Crops; Directorate of Economics & Statistics (DES), Ministry of Agriculture & Farmers Welfare.";

    public DemandStatusDto() {
    }

    public String getDemandStatus() { return demandStatus; }
    public String getMethodologyVersion() { return methodologyVersion; }

    @JsonProperty("isCalculated")
    public boolean isCalculated() { return isCalculated; }

    public Double getSurplusDeficitRatio() { return surplusDeficitRatio; }
    public Double getSurplusDeficitQuantity() { return surplusDeficitQuantity; }
    public String getRationale() { return rationale; }
    public String getOfficialCitations() { return officialCitations; }
}
