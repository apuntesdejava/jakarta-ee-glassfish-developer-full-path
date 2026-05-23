package com.mycompany.projecttracker.application.service;

import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.command.CreateTaskCommand;
import com.mycompany.projecttracker.application.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
import com.mycompany.projecttracker.application.port.out.ProjectRepository;
import com.mycompany.projecttracker.application.port.out.TaskNotificationSender;
import com.mycompany.projecttracker.application.result.ProjectResult;
import com.mycompany.projecttracker.application.result.TaskResult;
import com.mycompany.projecttracker.domain.model.AuditInfo;
import com.mycompany.projecttracker.domain.model.Project;
import com.mycompany.projecttracker.domain.model.Task;
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

    private static final Logger LOGGER = Logger.getLogger(ProjectApplicationService.class.getName());

    @Inject
    private ProjectRepository repository;

    @Inject
    private TaskNotificationSender taskNotificationSender;

    @Inject
    private Event<ProjectCreatedEvent> projectEvent;

    @Override
    public List<ProjectResult> findAll() {
        return repository.findAll()
            .map(this::toProjectResult)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ProjectResult> findById(Long id) {
        return repository.findById(id)
            .map(this::toProjectResult);
    }

    @Override
    public List<ProjectResult> findByStatus(String status) {
        return repository.findByStatus(status)
            .map(this::toProjectResult)
            .collect(Collectors.toList());
    }

    @Override
    public ProjectResult create(CreateProjectCommand command) {
        Project newProject = new Project();
        newProject.setName(command.name());
        newProject.setDescription(command.description());
        newProject.setStatus("Nuevo");
        newProject.setAuditInfo(new AuditInfo("admin_user", LocalDate.now()));

        Project savedProject = repository.save(newProject);
        ProjectResult createdProject = toProjectResult(savedProject);

        projectEvent.fire(new ProjectCreatedEvent(createdProject));
        LOGGER.info("--> Evento CDI disparado para Proyecto ID: " + createdProject.id());

        return createdProject;
    }

    @Override
    public TaskResult createTask(Long projectId, CreateTaskCommand command) {
        Task newTask = new Task();
        newTask.setTitle(command.title());
        newTask.setStatus("Pendiente");
        newTask.setAuditInfo(new AuditInfo("sistema", LocalDate.now()));

        Task savedTask = repository.createTask(projectId, newTask);
        taskNotificationSender.sendTaskCreated(projectId, savedTask.getId());

        LOGGER.info("--> JMS: Mensaje enviado a la cola para la tarea " + savedTask.getId());
        return toTaskResult(savedTask);
    }

    private ProjectResult toProjectResult(Project entity) {
        return new ProjectResult(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus()
        );
    }

    private TaskResult toTaskResult(Task entity) {
        return new TaskResult(
            entity.getId(),
            entity.getTitle(),
            entity.getStatus()
        );
    }
}
