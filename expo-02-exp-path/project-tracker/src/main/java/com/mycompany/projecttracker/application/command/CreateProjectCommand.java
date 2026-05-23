package com.mycompany.projecttracker.application.command;

/**
 * Application command used to create a project.
 *
 * @param name the project name
 * @param description the optional project description
 * @param status the status received from the external adapter
 */
public record CreateProjectCommand(
    String name,
    String description,
    String status
) {
}
