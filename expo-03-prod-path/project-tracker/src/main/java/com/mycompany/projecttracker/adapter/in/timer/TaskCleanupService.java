package com.mycompany.projecttracker.adapter.in.timer;

import com.mycompany.projecttracker.application.port.out.TaskRepository;
import jakarta.ejb.Schedule;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.logging.Logger;

@Singleton
@Startup
public class TaskCleanupService {

    private static final Logger LOGGER = Logger.getLogger(TaskCleanupService.class.getName());

    @Inject
    private TaskRepository taskRepository;

    @Schedule(hour = "*", minute = "*", second = "10", persistent = false)
    public void archiveOldTasks() {
        LOGGER.info("--> [JOB] Iniciando limpieza de tareas antiguas...");

        LocalDate thresholdDate = LocalDate.now().plusDays(1);
        int archivedTasks = taskRepository.archiveOldTasks("Completada", thresholdDate);

        if (archivedTasks == 0) {
            LOGGER.info("--> [JOB] El sistema está limpio. No hay tareas para archivar.");
            return;
        }

        LOGGER.info(() -> "--> [JOB] Se archivaron " + archivedTasks + " tareas antiguas.");
        LOGGER.info("--> [JOB] Limpieza finalizada.");
    }
}
