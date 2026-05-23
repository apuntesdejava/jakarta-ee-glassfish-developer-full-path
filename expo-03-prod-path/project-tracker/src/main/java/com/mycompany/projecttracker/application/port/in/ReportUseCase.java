package com.mycompany.projecttracker.application.port.in;

import java.util.concurrent.CompletableFuture;

/**
 * Input port for production-oriented report generation.
 */
public interface ReportUseCase {

    CompletableFuture<Void> generateReportAsync(Long projectId, String userInitiator);
}
