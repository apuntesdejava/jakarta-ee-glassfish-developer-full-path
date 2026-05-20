package com.mycompany.projecttracker.service;

import com.mycompany.projecttracker.entity.AuditInfo;
import com.mycompany.projecttracker.entity.Project;
import com.mycompany.projecttracker.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.mapper.ProjectMapper;
import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.repository.ProjectRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Application service that coordinates project business operations.
 */
@ApplicationScoped
@Transactional
public class ProjectService {

    /**
     * Logger used to trace domain events during the live demo.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectService.class.getName());

    /**
     * Jakarta Data repository used for persistence operations.
     */
    @Inject
    private ProjectRepository repository;

    /**
     * Mapper that translates between entities and API DTOs.
     */
    @Inject
    private ProjectMapper mapper;

    /**
     * CDI event channel used to notify UI integrations when a project is created.
     */
    @Inject
    private Event<ProjectCreatedEvent> projectEvent;

    /**
     * Returns all projects stored in the database.
     *
     * @return project DTOs ordered by the repository implementation
     */
    public List<ProjectDTO> findAll() {
        // Jakarta Data entrega un Stream; aquí se transforma el modelo persistente al contrato REST.
        return repository.findAll()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Finds a project by its identifier.
     *
     * @param id the project identifier
     * @return the project when it exists
     */
    public Optional<ProjectDTO> findById(Long id) {
        // El Optional del repositorio se conserva para que la capa REST decida entre 200 y 404.
        return repository.findById(id)
            .map(mapper::toDTO);
    }

    /**
     * Finds projects by lifecycle status.
     *
     * @param status the status to filter by
     * @return matching project DTOs
     */
    public List<ProjectDTO> findByStatus(String status) {
        // El método findByStatus se deriva por nombre; no hay consulta JPQL escrita a mano.
        return repository.findByStatus(status).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Creates a project and publishes a domain event for real-time UI updates.
     *
     * @param projectRequest the validated project data from the API or JSF form
     * @return the persisted project with its generated identifier
     */
    public ProjectDTO create(ProjectDTO projectRequest) {
        Project newEntity = mapper.toEntity(projectRequest);

        // En esta etapa el servicio centraliza los valores que no vienen del cliente.
        newEntity.setStatus("Nuevo");
        newEntity.setAuditInfo(new AuditInfo("admin_user", LocalDate.now()));

        // save devuelve la entidad administrada con el identificador generado por la base de datos.
        newEntity = repository.save(newEntity);

        ProjectDTO createdDto = mapper.toDTO(newEntity);

        // El evento conecta el caso de uso con WebSocket sin acoplar el servicio al transporte.
        projectEvent.fire(new ProjectCreatedEvent(createdDto));

        LOGGER.info("--> Evento CDI disparado para Proyecto ID: " + createdDto.id());

        return createdDto;
    }
}
