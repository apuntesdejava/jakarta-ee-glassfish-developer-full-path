package com.mycompany.projecttracker.service.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Logger;

/**
 * Message-driven bean that consumes task notification messages.
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/TaskQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationMDB implements MessageListener {

    /** Logger for message consumption activity. */
    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    /**
     * Handles one JMS message from the task queue.
     *
     * @param message JMS message delivered by the container
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();
                LOGGER.info("--> MDB Recibido: Procesando notificación para payload: " + payload);

                /*
                 * La pausa representa el envío externo de una notificación sin bloquear la
                 * petición que creó la tarea.
                 */
                Thread.sleep(2000);

                /*
                 * El payload contiene projectId:taskId y se usa para construir el mensaje final.
                 */
                String[] parts = payload.split(":");
                LOGGER.info("--> EMAIL ENVIADO: 'Nueva tarea creada en Proyecto " + parts[0] + " con ID " + parts[1] + "'");
            }
        } catch (JMSException | InterruptedException e) {
            LOGGER.severe("Error procesando mensaje JMS: " + e.getMessage());
        }
    }
}
