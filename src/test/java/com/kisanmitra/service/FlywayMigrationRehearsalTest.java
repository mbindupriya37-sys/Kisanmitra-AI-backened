package com.kisanmitra.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mysql://127.0.0.1:3306/kisanmitra_rehearsal_verify?createDatabaseIfNotExist=false&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
    "spring.flyway.enabled=true",
    "spring.flyway.baseline-on-migrate=true",
    "spring.flyway.baseline-version=1",
    "spring.flyway.baseline-description=Existing baseline schema (13 tables)",
    "spring.jpa.hibernate.ddl-auto=validate"
})
public class FlywayMigrationRehearsalTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testRealFlywayBaselineAndV2Migration() {
        // 1. Verify Flyway Schema History records
        List<Map<String, Object>> history = jdbcTemplate.queryForList(
                "SELECT installed_rank, version, description, type, script, success FROM flyway_schema_history ORDER BY installed_rank ASC"
        );

        assertNotNull(history, "Flyway schema history should exist");
        assertEquals(2, history.size(), "Flyway should have exactly 2 history entries (Baseline v1 and Migration v2)");

        // Entry 1: Automatic Baseline
        Map<String, Object> baselineEntry = history.get(0);
        assertEquals(1, ((Number) baselineEntry.get("installed_rank")).intValue());
        assertEquals("1", baselineEntry.get("version"));
        assertEquals("Existing baseline schema (13 tables)", baselineEntry.get("description"));
        assertEquals("BASELINE", baselineEntry.get("type"));
        Object success1 = baselineEntry.get("success");
        assertTrue(Boolean.TRUE.equals(success1) || Integer.valueOf(1).equals(success1));

        // Entry 2: Automatic V2 Migration
        Map<String, Object> v2Entry = history.get(1);
        assertEquals(2, ((Number) v2Entry.get("installed_rank")).intValue());
        assertEquals("2", v2Entry.get("version"));
        assertEquals("create supply demand tables", v2Entry.get("description"));
        assertEquals("SQL", v2Entry.get("type"));
        assertEquals("V2__create_supply_demand_tables.sql", v2Entry.get("script"));
        Object success2 = v2Entry.get("success");
        assertTrue(Boolean.TRUE.equals(success2) || Integer.valueOf(1).equals(success2));

        // 2. Verify all 13 baseline tables exist with preserved rows
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        Integer intentionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM farmer_crop_intentions", Integer.class);
        Integer cropCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM crops", Integer.class);
        Integer schemeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM government_schemes", Integer.class);

        assertEquals(20, userCount, "Baseline users must remain exactly 20");
        assertEquals(4, intentionCount, "Baseline farmer intentions must remain exactly 4");
        assertEquals(7, cropCount, "Baseline crops must remain exactly 7");
        assertEquals(7, schemeCount, "Baseline schemes must remain exactly 7");

        // 3. Verify all 6 additive tables were created by V2 and are empty
        Integer prodCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM crop_historical_production_observations", Integer.class);
        Integer mspCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM msp_price_notifications", Integer.class);
        Integer bufferCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public_buffer_stock_observations", Integer.class);
        Integer procCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM public_procurement_observations", Integer.class);
        Integer auditCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM data_import_audit_logs", Integer.class);
        Integer rejectedCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM data_import_rejected_rows", Integer.class);

        assertEquals(0, prodCount, "Additive historical production table must be empty");
        assertEquals(0, mspCount, "Additive MSP table must be empty");
        assertEquals(0, bufferCount, "Additive buffer stock table must be empty");
        assertEquals(0, procCount, "Additive procurement table must be empty");
        assertEquals(0, auditCount, "Additive audit log table must be empty");
        assertEquals(0, rejectedCount, "Additive rejected rows table must be empty");
    }
}
