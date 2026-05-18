package com.mycompany.projecttracker.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Project data transferred through REST and JSF layers.
 *
 * @param id project identifier
 * @param name project name
 * @param description project description
 * @param status project status
 */
public record ProjectDTO(
    Long id,

    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String name,

    @Size(max = 5000, message = "La descripción no puede exceder los 5000 caracteres")
    String description,

    String status
) {
    /*
     * El record concentra validación y transporte de datos; no necesita estado mutable ni
     * métodos adicionales.
     */
}
