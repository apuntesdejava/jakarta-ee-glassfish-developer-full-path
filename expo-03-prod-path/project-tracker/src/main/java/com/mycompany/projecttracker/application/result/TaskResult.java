package com.mycompany.projecttracker.application.result;

/**
 * Application result returned after creating a task.
 *
 * @param id the task identifier
 * @param title the task title
 * @param status the current task status
 */
public record TaskResult(
    Long id,
    String title,
    String status
) {
}
