package com.mycompany.projecttracker.domain.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Embedded audit metadata stored as part of a project row.
 *
 * @param createdBy the user that created the project
 * @param createdAt the date when the project was created
 */
@Embeddable
public record AuditInfo(
    String createdBy,
    LocalDate createdAt
) {
    /**
     * Creates an empty audit object required by Jakarta Persistence.
     */
    public AuditInfo() {
        this(null, null);
    }
}
