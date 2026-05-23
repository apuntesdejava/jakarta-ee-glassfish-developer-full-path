package com.mycompany.projecttracker.adapter.in.rest.mapper;

import com.mycompany.projecttracker.adapter.in.rest.dto.ProjectDTO;
import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.result.ProjectResult;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * CDI bean that maps between the REST contract and application models.
 */
@ApplicationScoped
public class ProjectRestMapper {

    /**
     * Converts an application result into the representation exposed by the REST API.
     *
     * @param result the project result to convert
     * @return the API DTO, or {@code null} when the result is {@code null}
     */
    public ProjectDTO toDTO(ProjectResult result) {
        if (result == null) {
            return null;
        }

        return new ProjectDTO(
            result.id(),
            result.name(),
            result.description(),
            result.status()
        );
    }

    /**
     * Converts a REST DTO into an application command.
     *
     * @param dto the DTO received from the API
     * @return a command ready for the application service
     */
    public CreateProjectCommand toCommand(ProjectDTO dto) {
        return new CreateProjectCommand(
            dto.name(),
            dto.description(),
            dto.status()
        );
    }
}
