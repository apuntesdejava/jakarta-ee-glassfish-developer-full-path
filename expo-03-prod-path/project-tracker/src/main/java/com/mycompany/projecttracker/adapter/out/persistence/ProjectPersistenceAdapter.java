package com.mycompany.projecttracker.adapter.out.persistence;

import com.mycompany.projecttracker.adapter.out.persistence.jakarta.ProjectDataRepository;
import com.mycompany.projecttracker.application.port.out.ProjectRepository;
import com.mycompany.projecttracker.domain.model.Project;
import com.mycompany.projecttracker.domain.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
@Transactional
public class ProjectPersistenceAdapter implements ProjectRepository {

    @Inject
    private ProjectDataRepository projectRepository;

    @PersistenceContext(unitName = "project-tracker-pu")
    private EntityManager em;

    @Override
    public Stream<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Stream<Project> findByStatus(String status) {
        return projectRepository.findByStatus(status).stream();
    }

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Task createTask(Long projectId, Task task) {
        Project project = em.find(Project.class, projectId);
        if (project == null) {
            throw new IllegalArgumentException("Proyecto no encontrado: " + projectId);
        }

        project.addTask(task);
        em.persist(task);
        em.flush();
        return task;
    }
}
