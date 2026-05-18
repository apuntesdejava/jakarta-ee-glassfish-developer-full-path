package com.mycompany.projecttracker.repository;

import com.mycompany.projecttracker.entity.Task;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Jakarta Data repository for task persistence operations.
 */
@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {

    /**
     * Finds tasks with a status and creation date older than the threshold.
     *
     * @param status status to filter by
     * @param thresholdDate exclusive creation-date threshold
     * @return matching old tasks
     */
    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.auditInfo.createdAt < :thresholdDate")
    List<Task> findOldTasks(@Param("status") String status, @Param("thresholdDate") LocalDate thresholdDate);
}
