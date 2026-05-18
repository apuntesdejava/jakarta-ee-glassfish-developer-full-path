package com.mycompany.projecttracker.repository;

import com.mycompany.projecttracker.entity.Project;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;
import java.util.List;

/**
 * Jakarta Data repository for project persistence operations.
 */
@Repository
public interface ProjectRepository extends BasicRepository<Project, Long> {

    /**
     * Finds projects that match the provided status.
     *
     * @param status status to filter by
     * @return projects with the requested status
     */
    List<Project> findByStatus(String status);
}
