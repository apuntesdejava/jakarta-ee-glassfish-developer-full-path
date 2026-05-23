package com.mycompany.projecttracker.application.service;

import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
import com.mycompany.projecttracker.application.port.out.ProjectRepository;
import com.mycompany.projecttracker.application.result.ProjectResult;
import com.mycompany.projecttracker.domain.model.AuditInfo;
import com.mycompany.projecttracker.domain.model.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Application service that coordinates project business operations.
 */
@ApplicationScoped
public class ProjectApplicationService implements ProjectUseCase {

    /**
     * Logger used to trace application events during the live demo.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectApplicationService.class.getName());

    /**
     * Output port used for persistence operations.
     */
    @Inject
    private ProjectRepository repository;

    /**
     * CDI event channel used to notify UI integrations when a project is created.
     */
    @Inject
    private Event<ProjectCreatedEvent> projectEvent;

    /**
     * Returns all projects stored in the database.
     *
     * @return project results ordered by the repository implementation
     */
    @Override
    public List<ProjectResult> findAll() {
        return repository.findAll()
            .map(this::toResult)
            .collect(Collectors.toList());
    }

    /**
     * Finds a project by its identifier.
     *
     * @param id the project identifier
     * @return the project when it exists
     */
    @Override
    public Optional<ProjectResult> findById(Long id) {
        return repository.findById(id)
            .map(this::toResult);
    }

    /**
     * Finds projects by lifecycle status.
     *
     * @param status the status to filter by
     * @return matching project results
     */
    @Override
    public List<ProjectResult> findByStatus(String status) {
        return repository.findByStatus(status)
            .map(this::toResult)
            .collect(Collectors.toList());
    }

    /**
     * Creates a project and publishes an application event for real-time UI updates.
     *
     * @param command the validated project data from a driving adapter
     * @return the persisted project with its generated identifier
     */
    @Override
    public ProjectResult create(CreateProjectCommand command) {
        Project newEntity = new Project();
        newEntity.setName(command.name());
        newEntity.setDescription(command.description());
        newEntity.setStatus("Nuevo");
        newEntity.setAuditInfo(new AuditInfo("admin_user", LocalDate.now()));

        Project savedProject = repository.save(newEntity);
        ProjectResult createdProject = toResult(savedProject);

        projectEvent.fire(new ProjectCreatedEvent(createdProject));
        LOGGER.info("--> Evento CDI disparado para Proyecto ID: " + createdProject.id());

        return createdProject;
    }

    private ProjectResult toResult(Project entity) {
        return new ProjectResult(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus()
        );
    }
}
