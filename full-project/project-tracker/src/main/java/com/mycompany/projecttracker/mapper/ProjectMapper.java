package com.mycompany.projecttracker.mapper;

import com.mycompany.projecttracker.entity.Project;
import com.mycompany.projecttracker.model.ProjectDTO;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Maps between persistent projects and API project DTOs.
 */
@ApplicationScoped
public class ProjectMapper {

    /**
     * Converts a project entity into a DTO.
     *
     * @param entity project entity to convert
     * @return DTO representation, or {@code null} when the entity is {@code null}
     */
    public ProjectDTO toDTO(Project entity) {
        if (entity == null) {
            return null;
        }
        /*
         * El DTO expone solo los datos que la API necesita devolver; los metadatos internos
         * permanecen en la entidad.
         */
        return new ProjectDTO(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getStatus()
        );
    }

    /**
     * Converts an API DTO into a new project entity.
     *
     * @param dto DTO received from the API or UI layer
     * @return entity ready to be persisted, or {@code null} when the DTO is {@code null}
     */
    public Project toEntity(ProjectDTO dto) {
        if (dto == null) {
            return null;
        }
        Project entity = new Project();
        /*
         * No se asigna el identificador porque la base de datos lo genera al persistir la
         * entidad.
         */
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setStatus(dto.status());
        return entity;
    }
}
