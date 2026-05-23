package com.mycompany.projecttracker.application.event;

import com.mycompany.projecttracker.application.result.ProjectResult;

/**
 * CDI application event published after a project is created successfully.
 *
 * @param project the created project result
 */
public record ProjectCreatedEvent(ProjectResult project) {
}
