package com.mycompany.projecttracker.adapter.out.messaging;

import com.mycompany.projecttracker.application.port.out.TaskNotificationSender;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;

@ApplicationScoped
public class JmsTaskNotificationSender implements TaskNotificationSender {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "java:app/jms/TaskQueue")
    private Queue taskQueue;

    @Override
    public void sendTaskCreated(Long projectId, Long taskId) {
        jmsContext.createProducer().send(taskQueue, projectId + ":" + taskId);
    }
}
