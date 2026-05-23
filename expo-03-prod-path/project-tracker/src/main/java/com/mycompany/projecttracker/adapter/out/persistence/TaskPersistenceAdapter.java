package com.mycompany.projecttracker.adapter.out.persistence;

import com.mycompany.projecttracker.adapter.out.persistence.jakarta.TaskDataRepository;
import com.mycompany.projecttracker.application.port.out.TaskRepository;
import com.mycompany.projecttracker.domain.model.Task;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
@Transactional
public class TaskPersistenceAdapter implements TaskRepository {

    @Inject
    private TaskDataRepository taskRepository;

    @Override
    public int archiveOldTasks(String status, LocalDate thresholdDate) {
        List<Task> tasksToArchive = taskRepository.findOldTasks(status, thresholdDate);

        for (Task task : tasksToArchive) {
            task.setStatus("Archivada");
        }

        return tasksToArchive.size();
    }
}
