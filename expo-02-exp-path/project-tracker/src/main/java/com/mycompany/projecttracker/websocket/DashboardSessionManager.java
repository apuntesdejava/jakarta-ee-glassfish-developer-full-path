package com.mycompany.projecttracker.websocket;
import com.mycompany.projecttracker.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.model.ProjectDTO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Application-scoped registry that broadcasts project events to connected WebSocket clients.
 */
@ApplicationScoped
public class DashboardSessionManager {

    /**
     * Logger used to trace WebSocket broadcasts during the demo.
     */
    private static final Logger LOGGER = Logger.getLogger(DashboardSessionManager.class.getName());

    /**
     * Active browser sessions connected to the dashboard endpoint.
     */
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    /**
     * JSON-B instance used to serialize outbound WebSocket messages.
     */
    private final Jsonb jsonb = JsonbBuilder.create();

    /**
     * Registers a connected browser session.
     *
     * @param session WebSocket session to register
     */
    public void addSession(Session session) {
        sessions.add(session);
    }

    /**
     * Removes a disconnected browser session.
     *
     * @param session WebSocket session to remove
     */
    public void removeSession(Session session) {
        sessions.remove(session);
    }

    /**
     * Observes created project events and broadcasts them to the dashboard.
     *
     * @param event domain event containing the created project
     */
    public void onProjectCreated(@Observes ProjectCreatedEvent event) {
        ProjectDTO newProject = event.project();
        LOGGER.info("--> [WebSocket] Recibido evento de nuevo proyecto: " + newProject.name());

        // JSON-B convierte el DTO al mensaje que consumen los navegadores conectados.
        String jsonMessage = jsonb.toJson(newProject);

        sendToAll(jsonMessage);
    }

    /**
     * Sends a text message to every open dashboard session.
     *
     * @param message JSON message to broadcast
     */
    private void sendToAll(String message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    // El envío asíncrono evita que un cliente lento bloquee a los demás.
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    LOGGER.warning("Error enviando websocket: " + e.getMessage());
                }
            }
        });
    }
}
