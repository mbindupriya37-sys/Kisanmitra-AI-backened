package com.kisanmitra.service;

import com.kisanmitra.dto.DataImportResultDto;
import com.kisanmitra.entity.Crop;
import com.kisanmitra.entity.DataImportAuditLog;
import com.kisanmitra.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupplyDemandImportValidationServiceTest {

    @Mock
    private DataImportAuditLogRepository auditLogRepository;

    @Mock
    private DataImportRejectedRowRepository rejectedRowRepository;

    @Mock
    private CropHistoricalProductionRepository productionRepository;

    @Mock
    private CropRepository cropRepository;

    @InjectMocks
    private SupplyDemandImportValidationService importValidationService;

    private Crop riceCrop;
    private Crop cottonCrop;

    @BeforeEach
    void setUp() {
        riceCrop = new Crop("Rice", "Oryza sativa", "Cereal");
        riceCrop.setId(1L);

        cottonCrop = new Crop("Cotton", "Gossypium", "Fiber");
        cottonCrop.setId(2L);
    }

    @Test
    void testDuplicateFileChecksumIsIdempotent() {
        String csv = "source_crop_name,crop_code,source_state,source_district,lgd_district_code,boundary_vintage,agricultural_year,season,area_ha,production_tonnes,yield_kg_ha,source_revision,source_org,publication_date,source_url\n" +
                     "RICE,RICE,Andhra Pradesh,Krishna,510,STANDARD_LGD_2024,2022-23,Kharif,240000,750000,3125,DES_FINAL_2022-23,DES_MoAFW,2024-01-10,https://desagri.gov.in\n";

        String checksum = importValidationService.calculateChecksum(csv);

        DataImportAuditLog existingLog = new DataImportAuditLog();
        existingLog.setExecutionStatus("SUCCESS");
        existingLog.setTotalRecordsParsed(1);
        existingLog.setFileChecksumSha256(checksum);

        when(auditLogRepository.findByFileChecksumSha256(checksum)).thenReturn(Optional.of(existingLog));

        DataImportResultDto result = importValidationService.validateAndProcessDesApyCsv("test_apy.csv", csv, true);

        assertEquals("ALREADY_IMPORTED", result.getStatus());
        assertEquals(0, result.getInsertedCount());
        verify(productionRepository, never()).save(any());
    }

    @Test
    void testAtomicRejectionWhenRowHasInvalidCrop() {
        String csv = "source_crop_name,crop_code,source_state,source_district,lgd_district_code,boundary_vintage,agricultural_year,season,area_ha,production_tonnes,yield_kg_ha,source_revision,source_org,publication_date,source_url\n" +
                     "UNKNOWN_CROP,NON_EXISTENT,State,District,100,STANDARD_LGD_2024,2022-23,Kharif,100,500,5000,DES_FINAL,DES,2024-01-01,https://desagri.gov.in\n";

        when(auditLogRepository.findByFileChecksumSha256(anyString())).thenReturn(Optional.empty());
        when(cropRepository.findByNameIgnoreCase("NON_EXISTENT")).thenReturn(Optional.empty());

        DataImportResultDto result = importValidationService.validateAndProcessDesApyCsv("invalid_crop.csv", csv, true);

        assertEquals("REJECTED", result.getStatus());
        assertEquals(1, result.getRejectedCount());
        assertEquals(0, result.getInsertedCount());
        assertFalse(result.getValidationErrors().isEmpty());
        verify(productionRepository, never()).save(any());
    }

    @Test
    void testPublishingDisabledSafetyGate() {
        String csv = "source_crop_name,crop_code,source_state,source_district,lgd_district_code,boundary_vintage,agricultural_year,season,area_ha,production_tonnes,yield_kg_ha,source_revision,source_org,publication_date,source_url\n" +
                     "RICE,RICE,Andhra Pradesh,Krishna,510,STANDARD_LGD_2024,2022-23,Kharif,240000,750000,3125,DES_FINAL_2022-23,DES_MoAFW,2024-01-10,https://desagri.gov.in\n";

        when(auditLogRepository.findByFileChecksumSha256(anyString())).thenReturn(Optional.empty());
        when(cropRepository.findByNameIgnoreCase("RICE")).thenReturn(Optional.of(riceCrop));

        // Note publishEnabled = false
        DataImportResultDto result = importValidationService.validateAndProcessDesApyCsv("valid_apy.csv", csv, false);

        assertEquals("VALIDATED_PUBLISH_DISABLED", result.getStatus());
        assertEquals(1, result.getTotalParsed());
        assertEquals(0, result.getInsertedCount());
        verify(productionRepository, never()).save(any());
    }
}
