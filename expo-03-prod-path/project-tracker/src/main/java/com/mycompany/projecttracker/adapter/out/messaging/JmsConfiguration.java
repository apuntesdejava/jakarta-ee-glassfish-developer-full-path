package com.mycompany.projecttracker.adapter.out.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.JMSConnectionFactoryDefinition;
import jakarta.jms.JMSDestinationDefinition;

@ApplicationScoped
@JMSConnectionFactoryDefinition(
    name = "java:app/jms/ProjectTrackerFactory",
    interfaceName = "jakarta.jms.ConnectionFactory"
)
@JMSDestinationDefinition(
    name = "java:app/jms/TaskQueue",
    interfaceName = "jakarta.jms.Queue",
    destinationName = "TaskQueuePhysical"
)
public class JmsConfiguration {
}
