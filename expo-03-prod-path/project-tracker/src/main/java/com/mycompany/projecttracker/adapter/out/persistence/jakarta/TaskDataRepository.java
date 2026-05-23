package com.mycompany.projecttracker.adapter.out.persistence.jakarta;

import com.mycompany.projecttracker.domain.model.Task;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Param;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskDataRepository extends CrudRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE t.status = :status AND t.auditInfo.createdAt < :thresholdDate")
    List<Task> findOldTasks(@Param("status") String status, @Param("thresholdDate") LocalDate thresholdDate);
}
