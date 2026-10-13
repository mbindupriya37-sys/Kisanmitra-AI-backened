package com.kisanmitra.repository;

import com.kisanmitra.entity.DataImportAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DataImportAuditLogRepository extends JpaRepository<DataImportAuditLog, Long> {
    Optional<DataImportAuditLog> findByFileChecksumSha256(String fileChecksumSha256);
}
