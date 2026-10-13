package com.kisanmitra.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestDatabaseIsolationGuardTest {

    @Test
    void testAllowsAllowlistedRehearsalDatabase() {
        assertDoesNotThrow(() -> {
            TestDatabaseIsolationGuard.validateUrl("jdbc:mysql://127.0.0.1:3306/kisanmitra_rehearsal_verify?useSSL=false&allowPublicKeyRetrieval=true");
        });
    }

    @Test
    void testFailsClosedOnWorkingDatabaseWithQueryParams() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            TestDatabaseIsolationGuard.validateUrl("jdbc:mysql://127.0.0.1:3306/kisanmitra?createDatabaseIfNotExist=true&useSSL=false");
        });
        assertTrue(ex.getMessage().contains("FAIL-CLOSED"));
        assertTrue(ex.getMessage().contains("kisanmitra"));
    }

    @Test
    void testFailsClosedOnWorkingDatabaseExact() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            TestDatabaseIsolationGuard.validateUrl("jdbc:mysql://127.0.0.1:3306/kisanmitra");
        });
        assertTrue(ex.getMessage().contains("FAIL-CLOSED"));
        assertTrue(ex.getMessage().contains("kisanmitra"));
    }

    @Test
    void testFailsClosedOnUnrecognizedDatabase() {
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            TestDatabaseIsolationGuard.validateUrl("jdbc:mysql://127.0.0.1:3306/unexpected_database_sandbox?useSSL=false");
        });
        assertTrue(ex.getMessage().contains("FAIL-CLOSED"));
        assertTrue(ex.getMessage().contains("unexpected_database_sandbox"));
    }

    @Test
    void testFailsClosedOnMissingOrEmptyDatasourceUrl() {
        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () -> {
            TestDatabaseIsolationGuard.validateUrl(null);
        });
        assertTrue(ex1.getMessage().contains("SAFETY VIOLATION (FAIL-CLOSED)"));

        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> {
            TestDatabaseIsolationGuard.validateUrl("   ");
        });
        assertTrue(ex2.getMessage().contains("SAFETY VIOLATION (FAIL-CLOSED)"));
    }
}
