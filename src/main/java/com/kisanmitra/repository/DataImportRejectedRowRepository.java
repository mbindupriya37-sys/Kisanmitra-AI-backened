package com.kisanmitra.repository;

import com.kisanmitra.entity.DataImportRejectedRow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataImportRejectedRowRepository extends JpaRepository<DataImportRejectedRow, Long> {
    List<DataImportRejectedRow> findByAuditLogId(Long auditLogId);
}
