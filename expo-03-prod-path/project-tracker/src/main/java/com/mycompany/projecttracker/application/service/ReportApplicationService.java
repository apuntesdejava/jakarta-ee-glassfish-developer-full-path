package com.mycompany.projecttracker.application.service;

import com.mycompany.projecttracker.application.port.in.ReportUseCase;
import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Application service that starts long-running report work on a managed executor.
 */
@ApplicationScoped
public class ReportApplicationService implements ReportUseCase {

    private static final Logger LOGGER = Logger.getLogger(ReportApplicationService.class.getName());

    @Resource(lookup = "java:app/concurrent/VirtualExecutor")
    private ManagedExecutorService executor;

    @Override
    public CompletableFuture<Void> generateReportAsync(Long projectId, String userInitiator) {
        return CompletableFuture.runAsync(() -> {
            try {
                LOGGER.info(() -> "--> Iniciando reporte para Proyecto ID: " + projectId + " solicitado por: " + userInitiator);
                LOGGER.info(() -> "--> Corriendo en Hilo: " + Thread.currentThread());
                Thread.sleep(5000);
                LOGGER.info(() -> "--> Reporte finalizado para Proyecto ID: " + projectId);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.severe("--> Reporte interrumpido");
            }
        }, executor);
    }
}
