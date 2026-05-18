package com.mycompany.projecttracker.service.timer;


import com.mycompany.projecttracker.entity.Task;
import com.mycompany.projecttracker.repository.TaskRepository;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

/**
 * Scheduled service that archives old completed tasks.
 */
@Singleton
@Startup
public class TaskCleanupService {

    /** Logger for scheduled cleanup activity. */
    private static final Logger logger = Logger.getLogger(TaskCleanupService.class.getName());

    /** Repository used to find cleanup candidates. */
    @Inject
    private TaskRepository taskRepository;

    /**
     * Archives completed tasks older than the configured threshold.
     */
    @Schedule(hour = "*", minute = "*", second = "10", persistent = false)
    @Transactional
    public void archiveOldTasks() {
        logger.info("--> [JOB] Iniciando limpieza de tareas antiguas...");

        /*
         * El umbral actual incluye datos de demostración creados hoy para que el job muestre
         * actividad durante la exposición.
         */
        LocalDate thresholdDate = LocalDate.now().plusDays(1);

        /*
         * Solo las tareas completadas y anteriores al umbral son candidatas para archivar.
         */
        List<Task> tasksToArchive = taskRepository.findOldTasks("Completada", thresholdDate);

        if (tasksToArchive.isEmpty()) {
            logger.info("--> [JOB] El sistema está limpio. No hay tareas para archivar.");
            return;
        }

        logger.info("--> [JOB] Se encontraron " + tasksToArchive.size() + " tareas antiguas.");

        /*
         * Al estar dentro de una transacción, JPA detecta el cambio de estado y sincroniza los
         * updates al finalizar el método.
         */
        for (Task task : tasksToArchive) {
            task.setStatus("Archivada");
            logger.info("----> Tarea " + task.getId() + " archivada.");
        }

        logger.info("--> [JOB] Limpieza finalizada.");
    }
}
