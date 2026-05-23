package com.mycompany.projecttracker.adapter.out.persistence;

import com.mycompany.projecttracker.adapter.out.persistence.jakarta.ProjectDataRepository;
import com.mycompany.projecttracker.application.port.out.ProjectRepository;
import com.mycompany.projecttracker.domain.model.Project;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Persistence adapter that implements the project repository port with Jakarta Data.
 */
@ApplicationScoped
@Transactional
public class ProjectPersistenceAdapter implements ProjectRepository {

    /**
     * Jakarta Data repository provided by the runtime.
     */
    @Inject
    private ProjectDataRepository projectRepository;

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
}
