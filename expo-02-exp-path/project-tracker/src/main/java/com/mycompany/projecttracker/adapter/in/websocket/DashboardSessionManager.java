package com.mycompany.projecttracker.adapter.in.websocket;

import com.mycompany.projecttracker.application.event.ProjectCreatedEvent;
import com.mycompany.projecttracker.application.result.ProjectResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Application-scoped registry that broadcasts project events to connected WebSocket clients.
 */
@ApplicationScoped
public class DashboardSessionManager {

    private static final Logger LOGGER = Logger.getLogger(DashboardSessionManager.class.getName());

    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    private final Jsonb jsonb = JsonbBuilder.create();

    public void addSession(Session session) {
        sessions.add(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void onProjectCreated(@Observes ProjectCreatedEvent event) {
        ProjectResult newProject = event.project();
        LOGGER.info("--> [WebSocket] Recibido evento de nuevo proyecto: " + newProject.name());

        String jsonMessage = jsonb.toJson(newProject);
        sendToAll(jsonMessage);
    }

    private void sendToAll(String message) {
        sessions.forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    LOGGER.warning("Error enviando websocket: " + e.getMessage());
                }
            }
        });
    }
}
