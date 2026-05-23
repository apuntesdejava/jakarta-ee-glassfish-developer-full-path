package com.mycompany.projecttracker.adapter.out.persistence.jakarta;

import com.mycompany.projecttracker.domain.model.Project;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;
import java.util.List;

/**
 * Jakarta Data repository that provides persistence operations for projects.
 */
@Repository
public interface ProjectDataRepository extends CrudRepository<Project, Long> {

    /**
     * Finds projects that match the given lifecycle status.
     *
     * @param status the status used as filter
     * @return projects with the requested status
     */
    List<Project> findByStatus(String status);
}
