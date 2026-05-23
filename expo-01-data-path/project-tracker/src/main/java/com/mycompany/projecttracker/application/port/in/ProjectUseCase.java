package com.mycompany.projecttracker.application.port.in;

import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.result.ProjectResult;

import java.util.List;
import java.util.Optional;

/**
 * Input port that exposes project use cases to driving adapters.
 */
public interface ProjectUseCase {

    List<ProjectResult> findAll();

    Optional<ProjectResult> findById(Long id);

    List<ProjectResult> findByStatus(String status);

    ProjectResult create(CreateProjectCommand command);
}
