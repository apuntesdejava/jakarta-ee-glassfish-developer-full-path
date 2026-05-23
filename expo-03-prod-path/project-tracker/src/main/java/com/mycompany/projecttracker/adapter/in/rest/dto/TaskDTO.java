package com.mycompany.projecttracker.adapter.in.rest.dto;

import jakarta.validation.constraints.NotNull;

public record TaskDTO(
    Long id,
    @NotNull String title,
    String status
) {
}
