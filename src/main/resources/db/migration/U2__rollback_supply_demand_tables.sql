-- ==============================================================================
-- KisanMitra AI: Phase 1 Crop Supply-Demand Intelligence
-- Standalone Rollback Script (U2)
-- Drops ONLY the 6 newly introduced additive tables in reverse dependency order
-- Leaves all 13 original baseline tables and data completely untouched
-- ==============================================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `data_import_rejected_rows`;
DROP TABLE IF EXISTS `data_import_audit_logs`;
DROP TABLE IF EXISTS `public_procurement_observations`;
DROP TABLE IF EXISTS `public_buffer_stock_observations`;
DROP TABLE IF EXISTS `msp_price_notifications`;
DROP TABLE IF EXISTS `crop_historical_production_observations`;

SET FOREIGN_KEY_CHECKS = 1;
