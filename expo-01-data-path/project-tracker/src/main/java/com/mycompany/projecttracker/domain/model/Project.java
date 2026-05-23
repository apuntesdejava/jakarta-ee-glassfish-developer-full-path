package com.mycompany.projecttracker.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Jakarta Persistence entity that represents a project managed by the application.
 */
@Entity
@Table(name = "PROJECT")
public class Project {

    /**
     * Database identifier generated when the project is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Human-readable project name.
     */
    @NotNull
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Optional long-form description of the project.
     */
    @Size(max = 5000)
    @Lob
    private String description;

    /**
     * Current lifecycle status for filtering and display.
     */
    @Column(length = 20)
    private String status;

    /**
     * Optional target date for completing the project.
     */
    private LocalDate deadline;

    /**
     * Audit metadata embedded in the same database row.
     */
    @Embedded
    private AuditInfo auditInfo;

    /**
     * Tasks owned by this project.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Creates an empty project instance required by Jakarta Persistence.
     */
    public Project() {
    }

    /**
     * Adds a task and keeps both sides of the bidirectional association synchronized.
     *
     * @param task the task to attach to this project
     */
    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    /**
     * Removes a task and clears its back-reference to this project.
     *
     * @param task the task to detach from this project
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    /**
     * Returns the project identifier.
     *
     * @return the generated project id
     */
    public Long getId() { return id; }

    /**
     * Updates the project identifier.
     *
     * @param id the generated project id
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Returns the project name.
     *
     * @return the project name
     */
    public String getName() { return name; }

    /**
     * Updates the project name.
     *
     * @param name the project name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Returns the project description.
     *
     * @return the project description
     */
    public String getDescription() { return description; }

    /**
     * Updates the project description.
     *
     * @param description the project description
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the project status.
     *
     * @return the project status
     */
    public String getStatus() { return status; }

    /**
     * Updates the project status.
     *
     * @param status the project status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns the project deadline.
     *
     * @return the project deadline
     */
    public LocalDate getDeadline() { return deadline; }

    /**
     * Updates the project deadline.
     *
     * @param deadline the project deadline
     */
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    /**
     * Returns audit metadata for this project.
     *
     * @return the audit metadata
     */
    public AuditInfo getAuditInfo() { return auditInfo; }

    /**
     * Updates audit metadata for this project.
     *
     * @param auditInfo the audit metadata
     */
    public void setAuditInfo(AuditInfo auditInfo) { this.auditInfo = auditInfo; }

    /**
     * Returns tasks owned by this project.
     *
     * @return mutable task list
     */
    public List<Task> getTasks() { return tasks; }

    /**
     * Replaces tasks owned by this project.
     *
     * @param tasks the task list
     */
    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
