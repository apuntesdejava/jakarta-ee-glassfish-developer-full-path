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

    public CreateProjectCommand toCommand(ProjectDTO dto) {
        return new CreateProjectCommand(
            dto.name(),
            dto.description(),
            dto.status()
        );
    }
}
