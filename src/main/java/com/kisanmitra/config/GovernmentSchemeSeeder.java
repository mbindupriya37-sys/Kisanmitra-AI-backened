package com.kisanmitra.config;

import com.kisanmitra.entity.GovernmentScheme;
import com.kisanmitra.entity.SchemeCategory;
import com.kisanmitra.repository.GovernmentSchemeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GovernmentSchemeSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(GovernmentSchemeSeeder.class);

    private final GovernmentSchemeRepository schemeRepository;

    public GovernmentSchemeSeeder(GovernmentSchemeRepository schemeRepository) {
        this.schemeRepository = schemeRepository;
    }

    @Override
    public void run(String... args) {
        if (schemeRepository.count() == 0) {
            logger.info("Seeding initial verified Indian Government Schemes...");

            List<GovernmentScheme> initialSchemes = List.of(
                    new GovernmentScheme(
                            "Pradhan Mantri Kisan Samman Nidhi (PM-KISAN)",
                            SchemeCategory.FINANCIAL_SUPPORT,
                            "Ministry of Agriculture & Farmers Welfare",
                            "Provides direct income support to landholding farmer families across India to supplement financial needs for agricultural inputs and domestic expenses.",
                            "All landholding farmer families with cultivable landholding in their names. Excludes institutional landholders, high income taxpayers, and former/current constitutional post holders.",
                            "Direct benefit transfer of ₹6,000 per year paid in three equal installments of ₹2,000 directly into Aadhaar-seeded bank accounts.",
                            "Aadhaar Card, Land Holding Document / Khatauni, Aadhaar-seeded Bank Passbook details, e-KYC verification.",
                            "Self-registration on the PM-KISAN official portal or via Common Service Centres (CSC) / State Nodal Officers.",
                            "https://pmkisan.gov.in/Documents/PM-KISAN_Guidelines.pdf",
                            "Ministry of Agriculture & Farmers Welfare (PM-KISAN Guidelines)",
                            "2026-10-08",
                            "VERIFIED"
                    ),
                    new GovernmentScheme(
                            "Pradhan Mantri Fasal Bima Yojana (PMFBY)",
                            SchemeCategory.CROP_INSURANCE,
                            "Ministry of Agriculture & Farmers Welfare",
                            "Provides comprehensive crop insurance coverage for non-preventable natural risks from pre-sowing to post-harvest stages.",
                            "All farmers including sharecroppers and tenant farmers growing notified crops in notified areas.",
                            "Subsidized premium rates: 2% for Kharif crops, 1.5% for Rabi crops, and 5% for Annual Commercial/Horticultural crops. Balance premium shared 50:50 by Central and State Governments.",
                            "Land Record Document (7/12 extract or Land Possession Certificate), Aadhaar Card, Bank Account Details, Sowing Certificate / Self-Declaration.",
                            "Online via PMFBY National Crop Insurance Portal, CSCs, or participating commercial banks/insurance intermediaries before cut-off date.",
                            "https://pmfby.gov.in/pdf/Revised_Operational_Guidelines.pdf",
                            "Ministry of Agriculture & Farmers Welfare (PMFBY Guidelines)",
                            "2026-10-08",
                            "VERIFIED"
                    ),
                    new GovernmentScheme(
                            "Kisan Credit Card (KCC) Scheme",
                            SchemeCategory.FINANCIAL_SUPPORT,
                            "Ministry of Agriculture & Farmers Welfare / Reserve Bank of India",
                            "Provides single-window credit support for short-term crop loans, post-harvest expenses, and working capital needs of farming and allied activities.",
                            "Individual/Joint farmers, Tenant farmers, Oral lessees, Sharecroppers, and Self-Help Groups (SHGs) of farmers.",
                            "Concessional interest rate of 7% per annum for loans up to ₹3 Lakhs, with an additional 3% Interest Subvention for prompt repayment (effective rate 4%). No collateral required for loans up to ₹1.60 Lakhs.",
                            "KCC Application Form, Land Ownership Document, Identity Proof (Aadhaar/Voter ID), Address Proof, Passport photo.",
                            "Apply at commercial bank branches, RRBs, Cooperative Banks, or online via PM-KISAN integrated portal.",
                            "https://www.rbi.org.in/Scripts/BS_ViewMasCirculardetails.aspx?id=12356",
                            "Reserve Bank of India (RBI Master Circular on KCC)",
                            "2026-10-08",
                            "VERIFIED"
                    ),
                    new GovernmentScheme(
                            "PM-KUSUM (Solar Energy Scheme)",
                            SchemeCategory.SOLAR_ENERGY,
                            "Ministry of New and Renewable Energy (MNRE)",
                            "Enables farmers to set up standalone solar agriculture water pumps and grid-connected solar power plants for clean energy and income generation.",
                            "Individual farmers, farmer groups, cooperatives, panchayats, and Farmer Producer Organizations (FPOs).",
                            "Capital support varies by state and DISCOM norms. Confirm exact subsidy share, beneficiary category, and application window on the official portal and current state action plan before applying.",
                            "Land Ownership Certificate, Bank Passbook, Aadhaar Card, Electricity Connection status (if applicable).",
                            "Submit application through State Renewable Energy Development Agencies (SREDA) or official PM-KUSUM portal.",
                            "https://pmkusum.mnre.gov.in/pdf/KUSUM_Guidelines.pdf",
                            "Ministry of New and Renewable Energy (MNRE Guidelines)",
                            "2026-10-08",
                            "VERIFICATION_REQUIRED"
                    ),
                    new GovernmentScheme(
                            "PMKSY - Per Drop More Crop (Micro Irrigation)",
                            SchemeCategory.IRRIGATION,
                            "Ministry of Agriculture & Farmers Welfare",
                            "Enhances water use efficiency at farm level through Per Drop More Crop (PDMC) micro-irrigation technologies (Drip & Sprinkler).",
                            "All categories of farmers owning cultivable land or holding valid lease agreement for at least 7 years.",
                            "Support levels vary by state, district, and annual action plan. Verify the exact subsidy rate, beneficiary category, and current availability before applying.",
                            "Land Records (RoR), Aadhaar Card, Bank Passbook, Soil & Water Testing Report (if required), Passport size photo.",
                            "Apply through State Agriculture / Horticulture Department portals or district horticulture offices.",
                            "https://pmksy.gov.in/microirrigation/Archive/Guidelines_PDMC_2022.pdf",
                            "Ministry of Agriculture & Farmers Welfare (PDMC Guidelines)",
                            "2026-10-08",
                            "VERIFICATION_REQUIRED"
                    ),
                    new GovernmentScheme(
                            "Soil Health Card (SHC) Scheme",
                            SchemeCategory.SOIL_HEALTH,
                            "Ministry of Agriculture & Farmers Welfare",
                            "Provides soil nutrient status reports and crop-wise fertilizer recommendations to optimize crop yield and preserve soil health.",
                            "All farmers across all Indian States and Union Territories.",
                            "Free soil sample testing (12 parameters including N, P, K, pH, EC, Organic Carbon, Micro-nutrients) and customized fertilizer recommendations every 3 years.",
                            "Land Identification Details (Survey Number), Aadhaar Card, Mobile Number.",
                            "Soil samples collected by Agriculture Department officials; Soil Health Cards distributed physically and accessible online via portal.",
                            "https://soilhealth.dac.gov.in/Content/SoilHealthCardSchemeGuidelines.pdf",
                            "Department of Agriculture & Farmers Welfare (SHC Guidelines)",
                            "2026-10-08",
                            "VERIFIED"
                    ),
                    new GovernmentScheme(
                            "Sub-Mission on Agricultural Mechanization (SMAM)",
                            SchemeCategory.MACHINERY,
                            "Ministry of Agriculture & Farmers Welfare",
                            "Increases reach of agricultural mechanization to small and marginal farmers and promotes setting up Custom Hiring Centres (CHCs).",
                            "Small, marginal, women, and SC/ST farmers; FPOs, SHGs, and agricultural entrepreneurs for Custom Hiring Centres.",
                            "Subsidy of 40% to 50% on purchase of approved farm equipment (tractors, power tillers, rotavators, seed drills, harvesters), and up to 80% for Custom Hiring Centres.",
                            "Land Records / RoR, Aadhaar Card, Caste Certificate (if applicable), Bank Passbook, Quotation of Machinery from authorized dealer.",
                            "Online registration on the Direct Benefit Transfer in Agricultural Mechanization portal.",
                            "https://agrimachinery.nic.in/Uploads/Guidelines/SMAM_Guidelines_Revised.pdf",
                            "Department of Agriculture & Farmers Welfare (SMAM Guidelines)",
                            "2026-10-08",
                            "VERIFIED"
                    )
            );

            schemeRepository.saveAll(initialSchemes);
            logger.info("Successfully seeded {} Indian Government Schemes.", initialSchemes.size());
        } else {
            logger.info("Government schemes already exist ({} records found). Skipping seeding.", schemeRepository.count());
        }
    }
}
