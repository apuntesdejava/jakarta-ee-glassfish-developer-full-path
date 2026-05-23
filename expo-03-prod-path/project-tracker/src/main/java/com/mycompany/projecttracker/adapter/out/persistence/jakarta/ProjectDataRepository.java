package com.mycompany.projecttracker.adapter.out.persistence.jakarta;

import com.mycompany.projecttracker.domain.model.Project;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.Repository;

import java.util.List;

@Repository
public interface ProjectDataRepository extends BasicRepository<Project, Long> {

    List<Project> findByStatus(String status);
}
