package com.mycompany.projecttracker.adapter.in.rest.mapper;

import com.mycompany.projecttracker.adapter.in.rest.dto.ProjectDTO;
import com.mycompany.projecttracker.adapter.in.rest.dto.TaskDTO;
import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.command.CreateTaskCommand;
import com.mycompany.projecttracker.application.result.ProjectResult;
import com.mycompany.projecttracker.application.result.TaskResult;
import jakarta.enterprise.context.ApplicationScoped;

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

    public TaskDTO toDTO(TaskResult result) {
        if (result == null) {
            return null;
        }

        return new TaskDTO(
            result.id(),
            result.title(),
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

    public CreateTaskCommand toCommand(TaskDTO dto) {
        return new CreateTaskCommand(
            dto.title(),
            dto.status()
        );
    }
}
