package com.mycompany.projecttracker.application.command;

/**
 * Application command used to create a task inside a project.
 *
 * @param title the task title
 * @param status the status received from the external adapter
 */
public record CreateTaskCommand(
    String title,
    String status
) {
}
