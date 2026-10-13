-- ==============================================================================
-- KisanMitra AI: Phase 1 Crop Supply-Demand Intelligence
-- Forward Additive Migration Script (V2)
-- Tested strictly against rehearsal verification database
-- Creates 6 additive tables (4 domain observation tables + 2 audit tables)
-- Leaves all 13 baseline tables completely untouched
-- ==============================================================================

-- 1. Historical Production Observations
CREATE TABLE IF NOT EXISTS `crop_historical_production_observations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `crop_id` BIGINT NOT NULL,
  `source_crop_name` VARCHAR(100) NOT NULL,
  `source_state` VARCHAR(100) NOT NULL,
  `source_district` VARCHAR(100) NOT NULL,
  `normalized_state` VARCHAR(100) NOT NULL,
  `normalized_district` VARCHAR(100) NOT NULL,
  `state_lgd_code` VARCHAR(20) DEFAULT NULL,
  `district_lgd_code` VARCHAR(20) DEFAULT NULL,
  `boundary_vintage` VARCHAR(50) NOT NULL,
  `agricultural_year` VARCHAR(20) NOT NULL,
  `season` VARCHAR(30) NOT NULL,
  `area_hectares` DECIMAL(14,4) DEFAULT NULL,
  `raw_production_value` DECIMAL(14,4) DEFAULT NULL,
  `raw_unit` VARCHAR(30) NOT NULL,
  `production_tonnes` DECIMAL(14,4) DEFAULT NULL,
  `yield_kg_per_ha` DECIMAL(14,4) DEFAULT NULL,
  `conversion_factor_applied` DECIMAL(8,4) DEFAULT NULL,
  `conversion_verification_ref` VARCHAR(255) DEFAULT NULL,
  `source_revision` VARCHAR(100) NOT NULL,
  `revision_priority` INT NOT NULL DEFAULT 10,
  `file_checksum_sha256` VARCHAR(64) NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  `superseded_by_batch_id` BIGINT DEFAULT NULL,
  `data_status` VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
  `source_organization` VARCHAR(100) NOT NULL,
  `source_reference_url` VARCHAR(500) DEFAULT NULL,
  `publication_date` DATE DEFAULT NULL,
  `import_timestamp` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_prod_obs_crop` FOREIGN KEY (`crop_id`) REFERENCES `crops` (`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_prod_obs_natural_identity` (`crop_id`, `normalized_state`, `normalized_district`, `boundary_vintage`, `agricultural_year`, `season`, `source_revision`),
  KEY `idx_prod_active_search` (`crop_id`, `normalized_state`, `normalized_district`, `season`, `is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 2. Statutory MSP Price Notifications
CREATE TABLE IF NOT EXISTS `msp_price_notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `crop_id` BIGINT NOT NULL,
  `commodity_name` VARCHAR(100) NOT NULL,
  `commodity_variety` VARCHAR(100) NOT NULL,
  `marketing_season_type` VARCHAR(20) NOT NULL,
  `season_year` VARCHAR(20) NOT NULL,
  `msp_inr_per_quintal` DECIMAL(10,2) NOT NULL,
  `bonus_inr_per_quintal` DECIMAL(10,2) DEFAULT 0.00,
  `cost_a2_fl_inr_per_quintal` DECIMAL(10,2) DEFAULT NULL,
  `cost_c2_inr_per_quintal` DECIMAL(10,2) DEFAULT NULL,
  `gazette_notification_ref` VARCHAR(150) NOT NULL,
  `effective_date` DATE NOT NULL,
  `is_active` BIT(1) NOT NULL DEFAULT b'1',
  `superseded_by_id` BIGINT DEFAULT NULL,
  `source_organization` VARCHAR(100) NOT NULL DEFAULT 'CCEA_GOI',
  `source_reference_url` VARCHAR(500) DEFAULT NULL,
  `publication_date` DATE DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_msp_crop` FOREIGN KEY (`crop_id`) REFERENCES `crops` (`id`) ON DELETE RESTRICT,
  UNIQUE KEY `uk_msp_notif_statutory_identity` (`crop_id`, `commodity_name`, `commodity_variety`, `marketing_season_type`, `season_year`, `gazette_notification_ref`),
  KEY `idx_msp_active_lookup` (`crop_id`, `marketing_season_type`, `season_year`, `is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 3. Public Buffer Stock Observations
CREATE TABLE IF NOT EXISTS `public_buffer_stock_observations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `commodity` VARCHAR(50) NOT NULL,
  `observation_date` DATE NOT NULL,
  `actual_central_pool_lmt` DECIMAL(12,4) NOT NULL,
  `operational_norm_lmt` DECIMAL(12,4) DEFAULT NULL,
  `strategic_reserve_norm_lmt` DECIMAL(12,4) DEFAULT NULL,
  `total_buffer_norm_lmt` DECIMAL(12,4) DEFAULT NULL,
  `norm_milestone_date` DATE DEFAULT NULL,
  `comparison_status` VARCHAR(50) NOT NULL,
  `surplus_deficit_lmt` DECIMAL(12,4) DEFAULT NULL,
  `notes` TEXT DEFAULT NULL,
  `source_organization` VARCHAR(100) NOT NULL DEFAULT 'DFPD_FCI',
  `source_reference_url` VARCHAR(500) DEFAULT NULL,
  `publication_date` DATE DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_buffer_stock_milestone` (`commodity`, `observation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 4. Public Procurement Observations
CREATE TABLE IF NOT EXISTS `public_procurement_observations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `commodity` VARCHAR(50) NOT NULL,
  `marketing_season_type` VARCHAR(20) NOT NULL,
  `marketing_season_year` VARCHAR(20) NOT NULL,
  `geographic_level` VARCHAR(30) NOT NULL DEFAULT 'NATIONAL',
  `state_name` VARCHAR(100) NOT NULL DEFAULT 'ALL_INDIA',
  `reporting_as_of_date` DATE NOT NULL,
  `procurement_stage` VARCHAR(30) NOT NULL,
  `progressive_quantity_lmt` DECIMAL(14,4) NOT NULL,
  `target_procurement_lmt` DECIMAL(14,4) DEFAULT NULL,
  `source_organization` VARCHAR(100) NOT NULL DEFAULT 'DFPD_FCI',
  `source_reference_url` VARCHAR(500) DEFAULT NULL,
  `publication_date` DATE DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_procurement_dated_snapshot` (`commodity`, `marketing_season_type`, `marketing_season_year`, `state_name`, `reporting_as_of_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 5. Data Import Audit Logs
CREATE TABLE IF NOT EXISTS `data_import_audit_logs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `domain_dataset` VARCHAR(50) NOT NULL,
  `source_file_name` VARCHAR(255) NOT NULL,
  `source_file_version` VARCHAR(50) NOT NULL,
  `file_checksum_sha256` VARCHAR(64) NOT NULL,
  `total_records_parsed` INT NOT NULL DEFAULT 0,
  `inserted_records` INT NOT NULL DEFAULT 0,
  `superseded_records` INT NOT NULL DEFAULT 0,
  `rejected_records` INT NOT NULL DEFAULT 0,
  `execution_status` VARCHAR(50) NOT NULL,
  `error_summary` TEXT DEFAULT NULL,
  `started_at` DATETIME(6) NOT NULL,
  `completed_at` DATETIME(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_import_audit_checksum` (`file_checksum_sha256`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 6. Data Import Rejected Rows
CREATE TABLE IF NOT EXISTS `data_import_rejected_rows` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `audit_log_id` BIGINT NOT NULL,
  `line_number` INT NOT NULL,
  `raw_line_content` TEXT NOT NULL,
  `rejection_reason` VARCHAR(255) NOT NULL,
  `rejected_field` VARCHAR(100) DEFAULT NULL,
  `created_at` DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_rejected_row_audit` FOREIGN KEY (`audit_log_id`) REFERENCES `data_import_audit_logs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
