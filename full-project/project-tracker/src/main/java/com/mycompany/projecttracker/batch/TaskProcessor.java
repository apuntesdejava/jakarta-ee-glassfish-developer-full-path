package com.mycompany.projecttracker.batch;

import com.mycompany.projecttracker.entity.AuditInfo;
import com.mycompany.projecttracker.entity.Project;
import com.mycompany.projecttracker.entity.Task;
import com.mycompany.projecttracker.repository.ProjectRepository;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Batch processor that converts raw CSV rows into task entities.
 */
@Named
@Dependent
public class TaskProcessor implements ItemProcessor {

    /** Repository used to verify the target project. */
    @Inject
    private ProjectRepository projectRepository;

    /** Logger for skipped and processed import rows. */
    private static final Logger LOGGER = Logger.getLogger(TaskProcessor.class.getName());

    /**
     * Parses one raw row and returns a task entity ready for writing.
     *
     * @param item raw CSV row
     * @return task entity or {@code null} to skip the item
     * @throws Exception when parsing fails
     */
    @Override
    public Object processItem(Object item) throws Exception {
        String line = (String) item;
        String[] parts = line.split(",");

        String title = parts[0];
        String status = parts[1];
        Long projectId = Long.parseLong(parts[2]);

        /*
         * La tarea importada solo continúa si el proyecto referenciado existe.
         */
        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            LOGGER.info("--> Batch Error: Proyecto ID " + projectId + " no encontrado. Saltando línea.");
            return null;
        }

        /*
         * Se arma la entidad con auditoría de importación para que el writer solo tenga que
         * persistirla.
         */
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setProject(projectOpt.get());
        task.setAuditInfo(new AuditInfo("batch_import", LocalDate.now()));

        return task;
    }
}
