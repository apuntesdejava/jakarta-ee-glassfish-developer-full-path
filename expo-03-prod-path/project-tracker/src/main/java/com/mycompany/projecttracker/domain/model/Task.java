package com.mycompany.projecttracker.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROJECT_ID", nullable = false)
    private Project project;

    @Embedded
    private AuditInfo auditInfo;

    public Task() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Project getProject() { return project; }

    public void setProject(Project project) { this.project = project; }

    public AuditInfo getAuditInfo() { return auditInfo; }

    public void setAuditInfo(AuditInfo auditInfo) { this.auditInfo = auditInfo; }
}
