package com.mycompany.projecttracker.entity;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Embeddable audit metadata shared by persistent entities.
 *
 * @param createdBy user or process that created the entity
 * @param createdAt date when the entity was created
 */
@Embeddable
public record AuditInfo(
    String createdBy,
    LocalDate createdAt
) {
    /**
     * Creates an empty audit value for JPA instantiation.
     */
    public AuditInfo() {
        /*
         * JPA necesita un constructor sin argumentos para materializar el componente embebido
         * antes de asignar los valores persistidos.
         */
        this(null, null);
    }
}
