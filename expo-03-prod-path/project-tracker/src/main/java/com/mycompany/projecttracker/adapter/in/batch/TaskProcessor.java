package com.mycompany.projecttracker.adapter.in.batch;

import com.mycompany.projecttracker.application.port.out.ProjectRepository;
import com.mycompany.projecttracker.domain.model.AuditInfo;
import com.mycompany.projecttracker.domain.model.Project;
import com.mycompany.projecttracker.domain.model.Task;
import jakarta.batch.api.chunk.ItemProcessor;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.time.LocalDate;
import java.util.Optional;
import java.util.logging.Logger;

@Named
@Dependent
public class TaskProcessor implements ItemProcessor {

    private static final Logger LOGGER = Logger.getLogger(TaskProcessor.class.getName());

    @Inject
    private ProjectRepository projectRepository;

    @Override
    public Object processItem(Object item) throws Exception {
        String line = (String) item;
        String[] parts = line.split(",");

        String title = parts[0];
        String status = parts[1];
        Long projectId = Long.valueOf(parts[2]);

        Optional<Project> projectOpt = projectRepository.findById(projectId);

        if (projectOpt.isEmpty()) {
            LOGGER.info(() -> "--> Batch Error: Proyecto ID " + projectId + " no encontrado. Saltando línea.");
            return null;
        }

        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setProject(projectOpt.get());
        task.setAuditInfo(new AuditInfo("batch_import", LocalDate.now()));

        return task;
    }
}
