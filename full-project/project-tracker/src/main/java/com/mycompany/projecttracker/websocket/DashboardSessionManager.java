package com.mycompany.projecttracker.websocket;

import com.mycompany.projecttracker.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.model.ProjectDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Tracks WebSocket sessions and broadcasts project creation events.
 */
@ApplicationScoped
public class DashboardSessionManager {

    /** Logger for dashboard push activity. */
    private static final Logger LOGGER = Logger.getLogger(DashboardSessionManager.class.getName());

    /** Connected browser sessions. */
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    /** JSON-B serializer used to produce WebSocket payloads. */
    private final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Adds a connected dashboard session.
     *
     * @param session WebSocket session to track
     */
    public void addSession(Session session) {
        sessions.add(session);
    }

    /**
     * Removes a disconnected dashboard session.
     *
     * @param session WebSocket session to remove
     */
    public void removeSession(Session session) {
        sessions.remove(session);
    }

    /**
     * Handles project creation events and broadcasts them to dashboards.
     *
     * @param event CDI event with the created project
     */
    public void onProjectCreated(@Observes ProjectCreatedEvent event) {
        ProjectDTO newProject = event.project();
        LOGGER.info("--> [WebSocket] Recibido evento de nuevo proyecto: " + newProject.name());

        /*
         * El DTO se serializa como JSON para que el navegador pueda insertarlo en la tabla sin
         * recargar la página.
         */
        String jsonMessage = jsonb.toJson(newProject);

        sendToAll(jsonMessage);
    }

    /**
     * Sends a text message to every open dashboard session.
     *
     * @param message JSON message to send
     */
    private void sendToAll(String message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    /*
                     * El envío asíncrono evita que un cliente lento bloquee al resto de sesiones.
                     */
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    LOGGER.warning("Error enviando websocket: " + e.getMessage());
                }
            }
        });
    }
}
