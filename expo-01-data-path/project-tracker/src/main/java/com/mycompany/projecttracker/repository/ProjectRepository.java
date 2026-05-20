package com.mycompany.projecttracker.repository;

import com.mycompany.projecttracker.entity.Project;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;
import java.util.List;

/**
 * Jakarta Data repository that provides persistence operations for projects.
 */
@Repository
public interface ProjectRepository extends BasicRepository<Project, Long> {

    /**
     * Finds projects that match the given lifecycle status.
     *
     * @param status the status used as filter
     * @return projects with the requested status
     */
    List<Project> findByStatus(String status);
}
