package com.mycompany.projecttracker.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Jakarta Persistence entity that represents a task belonging to a project.
 */
@Entity
@Table(name = "TASK")
public class Task {

    /**
     * Database identifier generated when the task is persisted.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short task title.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Current lifecycle status for this task.
     */
    private String status;

    /**
     * Project that owns this task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private Project project;

    /**
     * Creates an empty task instance required by Jakarta Persistence.
     */
    public Task() {
    }

    /**
     * Returns the task identifier.
     *
     * @return the generated task id
     */
    public Long getId() { return id; }

    /**
     * Updates the task identifier.
     *
     * @param id the generated task id
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Returns the task title.
     *
     * @return the task title
     */
    public String getTitle() { return title; }

    /**
     * Updates the task title.
     *
     * @param title the task title
     */
    public void setTitle(String title) { this.title = title; }

    /**
     * Returns the task status.
     *
     * @return the task status
     */
    public String getStatus() { return status; }

    /**
     * Updates the task status.
     *
     * @param status the task status
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Returns the owning project.
     *
     * @return the owning project
     */
    public Project getProject() { return project; }

    /**
     * Updates the owning project.
     *
     * @param project the owning project
     */
    public void setProject(Project project) { this.project = project; }
}
