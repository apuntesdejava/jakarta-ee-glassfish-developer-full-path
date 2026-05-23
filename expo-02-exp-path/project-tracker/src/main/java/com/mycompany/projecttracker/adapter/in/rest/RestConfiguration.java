package com.mycompany.projecttracker.adapter.in.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Configures the base path for all Jakarta REST resources in the application.
 */
@ApplicationPath("resources")
public class RestConfiguration extends Application {
}
