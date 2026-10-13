package com.kisanmitra.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Safety Guard: Enforces fail-closed explicit database allowlisting for test runs.
 * Only specifically designated isolated verification/sandbox databases are permitted.
 * Any other database (including working DB 'kisanmitra') causes an immediate hard abort.
 */
@Configuration
public class TestDatabaseIsolationGuard {

    private static final Set<String> ALLOWED_TEST_DATABASES = Set.of(
            "kisanmitra_rehearsal_verify"
    );

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @PostConstruct
    public void validateTestDatabaseIsolation() {
        validateUrl(datasourceUrl);
    }

    public static void validateUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalStateException(
                    "SAFETY VIOLATION (FAIL-CLOSED): 'spring.datasource.url' is missing in test environment! " +
                    "Tests cannot proceed without an explicit isolated test database configuration."
            );
        }

        String dbName = extractDatabaseName(url);
        if (dbName == null || !ALLOWED_TEST_DATABASES.contains(dbName.toLowerCase())) {
            throw new IllegalStateException(
                    "CRITICAL SAFETY ABORT (FAIL-CLOSED): Configured test database '" + dbName +
                    "' is not in the approved test database allowlist " + ALLOWED_TEST_DATABASES +
                    "! Working database 'kisanmitra' and all unexpected databases are strictly forbidden during tests."
            );
        }
    }

    public static String extractDatabaseName(String url) {
        if (url == null) return null;
        String clean = url.trim();
        int qIdx = clean.indexOf('?');
        if (qIdx != -1) {
            clean = clean.substring(0, qIdx);
        }
        int slashIdx = clean.lastIndexOf('/');
        if (slashIdx != -1 && slashIdx + 1 < clean.length()) {
            return clean.substring(slashIdx + 1).trim();
        }
        return null;
    }
}
