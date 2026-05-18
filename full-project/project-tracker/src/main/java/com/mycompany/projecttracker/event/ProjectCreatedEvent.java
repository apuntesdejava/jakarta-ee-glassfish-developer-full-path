package com.mycompany.projecttracker.event;

import com.mycompany.projecttracker.model.ProjectDTO;

/**
 * CDI event published after a project is created.
 *
 * @param project project that was created
 */
public record ProjectCreatedEvent(ProjectDTO project) {
}
