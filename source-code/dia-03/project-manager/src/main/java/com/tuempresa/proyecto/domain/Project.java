package com.tuempresa.proyecto.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank; // Para validaciones, lo veremos en el Día 6
import jakarta.validation.constraints.Size;
import java.time.LocalDate; // Para fechas, Jakarta EE soporta java.time

@Entity // Marca la clase como una entidad Jakarta Persistence, se mapeará a una tabla llamada 'Project' por defecto
@Table(name = "PROJECTS") // Opcional: Especifica el nombre de la tabla si es diferente al nombre de la clase
public class Project extends BaseEntity {

    @NotBlank(message = "El nombre del proyecto no puede estar vacío.")
    @Size(max = 100, message = "El nombre del proyecto no puede exceder 100 caracteres.")
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    // Constructor vacío (requerido por Jakarta Persistence)
    public Project() {
    }

    // Constructor con parámetros (opcional, para conveniencia)
    public Project(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Project{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", description='" + description + '\''
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + '}';
    }
}
