package com.mycompany.projecttracker.service;

import com.mycompany.projecttracker.entity.AuditInfo;
import com.mycompany.projecttracker.entity.Project;
import com.mycompany.projecttracker.entity.Task;
import com.mycompany.projecttracker.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.mapper.ProjectMapper;
import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.model.TaskDTO;
import com.mycompany.projecttracker.repository.ProjectRepository;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Coordinates project and task use cases across repositories, events, and messaging.
 */
@ApplicationScoped
@Transactional
public class ProjectService {

    /** Logger for project service operations. */
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());

    /** Repository used to persist and query projects. */
    @Inject
    private ProjectRepository repository;

    /** Mapper that converts between project entities and DTOs. */
    @Inject
    private ProjectMapper mapper;

    /** JMS context used to publish task notifications. */
    @Inject
    private JMSContext jmsContext;

    /** Queue that receives task notification messages. */
    @Resource(lookup = "java:app/jms/TaskQueue")
    private Queue taskQueue;

    /** Entity manager used for task persistence in the aggregate relationship. */
    @PersistenceContext(unitName = "project-tracker-pu")
    private EntityManager em;

    /** CDI event publisher for newly created projects. */
    @Inject
    private Event<ProjectCreatedEvent> projectEvent;

    /**
     * Lists every project.
     *
     * @return project DTOs ordered by the repository implementation
     */
    public List<ProjectDTO> findAll() {
        /*
         * El repositorio devuelve entidades y el mapper reduce el resultado al contrato público
         * de la aplicación.
         */
        return repository.findAll()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Finds one project by identifier.
     *
     * @param id project identifier
     * @return project DTO when it exists
     */
    public Optional<ProjectDTO> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDTO);
    }

    /**
     * Lists projects that match a status.
     *
     * @param status status to filter by
     * @return project DTOs with the requested status
     */
    public List<ProjectDTO> findByStatus(String status) {
        return repository.findByStatus(status).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Creates a project and publishes the corresponding CDI event.
     *
     * @param projectRequest requested project data
     * @return persisted project DTO
     */
    public ProjectDTO create(ProjectDTO projectRequest) {
        /*
         * Se completa la información controlada por el servidor antes de guardar la entidad.
         */
        Project newEntity = mapper.toEntity(projectRequest);
        newEntity.setStatus("Nuevo");
        newEntity.setAuditInfo(new AuditInfo("admin_user", LocalDate.now()));

        newEntity = repository.save(newEntity);

        ProjectDTO createdDto = mapper.toDTO(newEntity);

        /*
         * El evento sincroniza a los observadores internos, como el dashboard WebSocket.
         */
        projectEvent.fire(new ProjectCreatedEvent(createdDto));

        LOGGER.info("--> Evento CDI disparado para Proyecto ID: " + createdDto.id());

        return createdDto;
    }

    /**
     * Creates a task for a project and sends a JMS notification.
     *
     * @param projectId owning project identifier
     * @param taskDto requested task data
     * @return persisted task DTO
     */
    public TaskDTO createTask(Long projectId, TaskDTO taskDto) {
        /*
         * Primero se carga el proyecto dueño para mantener consistente la relación
         * bidireccional antes de persistir la tarea.
         */
        Project project = repository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + projectId));

        Task newTask = new Task();
        newTask.setTitle(taskDto.title());
        newTask.setStatus("Pendiente");


        newTask.setAuditInfo(new AuditInfo("sistema", LocalDate.now()));

        project.addTask(newTask);

        em.persist(newTask);
        em.flush();
        String messagePayload = project.getId() + ":" + newTask.getId();

        /*
         * El mensaje desacopla la creación de la tarea del proceso posterior de notificación.
         */
        jmsContext.createProducer().send(taskQueue, messagePayload);

        LOGGER.info("--> JMS: Mensaje enviado a la cola para la tarea " + newTask.getId());

        return new TaskDTO(newTask.getId(), newTask.getTitle(), newTask.getStatus());
    }
}
