package com.mycompany.projecttracker.websocket;

import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.logging.Logger;

/**
 * WebSocket endpoint used by the project dashboard.
 */
@ServerEndpoint("/ws/dashboard")
public class ProjectDashboardEndpoint {

    /**
     * Logger used to trace client connections during the demo.
     */
    private static final Logger LOGGER = Logger.getLogger(ProjectDashboardEndpoint.class.getName());

    /**
     * Shared manager that tracks active dashboard sessions.
     */
    @Inject
    private DashboardSessionManager sessionManager;

    /**
     * Registers a browser when it opens a WebSocket connection.
     *
     * @param session opened WebSocket session
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("--> Nuevo cliente WebSocket conectado: " + session.getId());
        sessionManager.addSession(session);
    }

    /**
     * Removes a browser when it closes the WebSocket connection.
     *
     * @param session closed WebSocket session
     */
    @OnClose
    public void onClose(Session session) {
        sessionManager.removeSession(session);
    }
}
