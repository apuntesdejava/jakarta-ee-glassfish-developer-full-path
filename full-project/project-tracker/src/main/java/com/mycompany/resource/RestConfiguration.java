package com.mycompany.resource;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures RESTful web services for the application.
 */
@ApplicationPath("resources")
public class RestConfiguration extends Application {
    /*
     * La clase extiende Application para fijar la ruta base de JAX-RS mediante anotación.
     */
}
