package com.mycompany.projecttracker.service;

import jakarta.annotation.Resource;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

/**
 * Generates project reports on the managed executor.
 */
@ApplicationScoped
public class ReportService {

    /** Logger for report generation progress. */
    private static final Logger logger = Logger.getLogger(ReportService.class.getName());

    /** Managed executor configured to run asynchronous work. */
    @Resource(lookup = "java:app/concurrent/VirtualExecutor")
    private ManagedExecutorService executor;

    /**
     * Starts report generation asynchronously.
     *
     * @param projectId project identifier
     * @param userInitiator user who requested the report
     * @return future that completes when the background task finishes
     */
    public CompletableFuture<Void> generateReportAsync(Long projectId, String userInitiator) {

        /*
         * CompletableFuture entrega el trabajo al ejecutor administrado para respetar el ciclo de
         * vida y el contexto del servidor Jakarta EE.
         */
        return CompletableFuture.runAsync(() -> {
            try {
                /*
                 * La espera simula una operación costosa, como armar un PDF o consultar datos
                 * agregados.
                 */
                logger.info("--> Iniciando reporte para Proyecto ID: " + projectId + " solicitado por: " + userInitiator);

                logger.info("--> Corriendo en Hilo: " + Thread.currentThread());

                Thread.sleep(5000);

                /*
                 * Aquí se cerraría el flujo real del reporte, por ejemplo persistiendo el
                 * resultado o notificando al usuario.
                 */
                logger.info("--> Reporte finalizado para Proyecto ID: " + projectId);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.severe("--> Reporte interrumpido");
            }
        }, executor);
    }
}
