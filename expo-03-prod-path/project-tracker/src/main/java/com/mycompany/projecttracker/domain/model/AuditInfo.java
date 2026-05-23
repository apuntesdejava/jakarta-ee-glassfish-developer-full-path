package com.mycompany.projecttracker.domain.model;

import jakarta.persistence.Embeddable;
import java.time.LocalDate;

/**
 * Embedded audit metadata stored as part of a project or task row.
 *
 * @param createdBy the user that created the item
 * @param createdAt the date when the item was created
 */
@Embeddable
public record AuditInfo(
    String createdBy,
    LocalDate createdAt
) {
    public AuditInfo() {
        this(null, null);
    }
}
