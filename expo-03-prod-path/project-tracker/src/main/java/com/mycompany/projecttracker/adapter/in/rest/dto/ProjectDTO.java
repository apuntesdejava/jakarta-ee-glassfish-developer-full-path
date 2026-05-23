package com.mycompany.projecttracker.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * REST data transfer object used to create and read projects.
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
}
