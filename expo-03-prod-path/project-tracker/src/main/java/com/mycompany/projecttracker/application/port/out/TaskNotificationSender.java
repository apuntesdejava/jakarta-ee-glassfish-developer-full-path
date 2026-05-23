package com.mycompany.projecttracker.application.port.out;

/**
 * Output port used to notify asynchronous integrations about task creation.
 */
public interface TaskNotificationSender {

    void sendTaskCreated(Long projectId, Long taskId);
}
