package com.mycompany.projecttracker.mapper;

import com.mycompany.projecttracker.entity.Project;
import com.mycompany.projecttracker.model.ProjectDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * CDI bean that maps between the persistence entity and the REST DTO.
 */
@ApplicationScoped
public class ProjectMapper {

    /**
     * Converts a project entity into the representation exposed by the REST API.
     *
     * @param entity the project entity to convert
     * @return the API DTO, or {@code null} when the entity is {@code null}
     */
    public ProjectDTO toDTO(Project entity) {
        if (entity == null) {
            return null;
        }
        // El DTO mantiene solo los campos que forman parte del contrato HTTP de esta etapa.
        return new ProjectDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus()
        );
    }

    /**
     * Converts a REST DTO into a new project entity.
     *
     * @param dto the DTO received from the API
     * @return a project entity ready for business defaults and persistence
     */
    public Project toEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        Project entity = new Project();
        // El identificador se deja vacío porque la base de datos lo genera al persistir.
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setStatus(dto.status());
        return entity;
    }
}
