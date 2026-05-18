package com.mycompany.projecttracker.model;

import jakarta.validation.constraints.NotNull;

/**
 * Task data transferred through REST and batch-facing APIs.
 *
 * @param id task identifier
 * @param title task title
 * @param status task status
 */
public record TaskDTO(
    Long id,
    @NotNull String title,
    String status
) {}
