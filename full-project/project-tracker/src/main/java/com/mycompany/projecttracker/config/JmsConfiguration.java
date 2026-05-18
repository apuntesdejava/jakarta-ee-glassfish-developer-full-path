package com.mycompany.projecttracker.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.jms.JMSConnectionFactoryDefinition;
import jakarta.jms.JMSDestinationDefinition;

/**
 * Declares the JMS resources used by the project tracker application.
 */
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
    /*
     * La configuración vive en anotaciones para que el servidor cree la fábrica de conexión y
     * la cola física durante el despliegue.
     */
}
