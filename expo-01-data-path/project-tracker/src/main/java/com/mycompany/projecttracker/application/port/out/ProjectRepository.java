package com.mycompany.projecttracker.application.port.out;

import com.mycompany.projecttracker.domain.model.Project;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Output port required by the application to persist and query projects.
 */
public interface ProjectRepository {

    Stream<Project> findAll();

    Optional<Project> findById(Long id);

    Stream<Project> findByStatus(String status);

    Project save(Project project);
}
