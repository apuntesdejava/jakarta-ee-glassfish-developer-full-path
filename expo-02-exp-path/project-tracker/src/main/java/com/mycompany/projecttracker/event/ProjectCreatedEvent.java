package com.mycompany.projecttracker.event;

import com.mycompany.projecttracker.model.ProjectDTO;

/**
 * CDI domain event published after a project is created successfully.
 *
 * @param project the created project DTO
 */
public record ProjectCreatedEvent(ProjectDTO project) {
}
