package com.mycompany.projecttracker.web;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;

/**
 * Enables Jakarta Faces integration for the application.
 */
@FacesConfig
@ApplicationScoped
public class FacesConfiguration {
    // La configuración declarativa evita mantener archivos XML específicos para Faces.
}
