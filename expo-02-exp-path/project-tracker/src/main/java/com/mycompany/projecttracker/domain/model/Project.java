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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 3, max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 5000)
    @Lob
    private String description;

    @Column(length = 20)
    private String status;

    private LocalDate deadline;

    @Embedded
    private AuditInfo auditInfo;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setProject(null);
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDate getDeadline() { return deadline; }

    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public AuditInfo getAuditInfo() { return auditInfo; }

    public void setAuditInfo(AuditInfo auditInfo) { this.auditInfo = auditInfo; }

    public List<Task> getTasks() { return tasks; }

    public void setTasks(List<Task> tasks) { this.tasks = tasks; }
}
