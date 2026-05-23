package com.mycompany.projecttracker.application.port.out;

import java.time.LocalDate;

/**
 * Output port required by scheduled task maintenance.
 */
public interface TaskRepository {

    int archiveOldTasks(String status, LocalDate thresholdDate);
}
