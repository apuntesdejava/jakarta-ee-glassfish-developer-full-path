package com.mycompany.projecttracker.websocket;

import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.util.logging.Logger;

/**
 * WebSocket endpoint used by browser dashboards.
 */
@ServerEndpoint("/ws/dashboard")
public class ProjectDashboardEndpoint {

    /** Logger for WebSocket connection lifecycle events. */
    private static final Logger LOGGER = Logger.getLogger(ProjectDashboardEndpoint.class.getName());

    /** Session registry shared by dashboard endpoint instances. */
    @Inject
    private DashboardSessionManager sessionManager;

    /**
     * Registers a newly opened WebSocket session.
     *
     * @param session connected browser session
     */
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.info("--> Nuevo cliente WebSocket conectado: " + session.getId());
        sessionManager.addSession(session);
    }

    /**
     * Unregisters a closed WebSocket session.
     *
     * @param session disconnected browser session
     */
    @OnClose
    public void onClose(Session session) {
        /*
         * Al quitar la sesión cerrada se evita intentar enviar mensajes a conexiones inválidas.
         */
        sessionManager.removeSession(session);
    }
}
