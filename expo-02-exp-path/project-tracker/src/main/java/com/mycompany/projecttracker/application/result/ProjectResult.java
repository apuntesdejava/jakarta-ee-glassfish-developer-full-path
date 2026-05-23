package com.mycompany.projecttracker.application.result;

/**
 * Application result returned by project use cases.
 *
 * @param id the project identifier
 * @param name the project name
 * @param description the optional project description
 * @param status the current project status
 */
public record ProjectResult(
    Long id,
    String name,
    String description,
    String status
) {
}
