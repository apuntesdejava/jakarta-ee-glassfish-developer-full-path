package com.mycompany.projecttracker.adapter.in.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

import java.util.logging.Logger;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/TaskQueue"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "jakarta.jms.Queue")
})
public class NotificationMDB implements MessageListener {

    private static final Logger LOGGER = Logger.getLogger(NotificationMDB.class.getName());

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage textMessage) {
                String payload = textMessage.getText();
                LOGGER.info(() -> "--> MDB Recibido: Procesando notificación para payload: " + payload);

                Thread.sleep(2000);

                String[] parts = payload.split(":");
                LOGGER.info(() -> "--> EMAIL ENVIADO: 'Nueva tarea creada en Proyecto " + parts[0] + " con ID " + parts[1] + "'");
            }
        } catch (JMSException | InterruptedException e) {
            LOGGER.severe(() -> "Error procesando mensaje JMS: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
