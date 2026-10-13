package com.kisanmitra.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Asynchronous Advisory Telemetry and Escalation Audit Service.
 * Dispatches non-critical advisory and audit events to background threads (km-async-*),
 * preserving synchronous HTTP request latency and strict error isolation.
 */
@Service
public class AsyncAdvisoryAuditService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncAdvisoryAuditService.class);

    @Async("kisanmitraAsyncExecutor")
    public CompletableFuture<Boolean> recordAdvisoryEventAsync(String eventType, String category, String outcomeStatus) {
        try {
            logger.info("Asynchronous advisory event [{}] dispatched on thread [{}] for category [{}], status [{}]",
                    eventType, Thread.currentThread().getName(), category, outcomeStatus);

            if ("TRIGGER_TEST_FAILURE".equalsIgnoreCase(eventType)) {
                throw new IllegalStateException("Simulated asynchronous failure for telemetry error handling test");
            }

            return CompletableFuture.completedFuture(true);
        } catch (Exception ex) {
            logger.warn("Asynchronous advisory event dispatch failed for type [{}]: {}", eventType, ex.getMessage());
            CompletableFuture<Boolean> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }
}
