package com.mycompany.projecttracker.web;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;
import jakarta.security.enterprise.identitystore.InMemoryIdentityStoreDefinition;

/**
 * Declares application roles and in-memory users for the demo application.
 */
@FacesConfig
@ApplicationScoped
@DeclareRoles({"ADMIN", "USER"})
@InMemoryIdentityStoreDefinition({
    @InMemoryIdentityStoreDefinition.Credentials(
        callerName = "admin", password = "admin123", groups = {"ADMIN", "USER"}
    ),
    @InMemoryIdentityStoreDefinition.Credentials(
        callerName = "pepe", password = "pepe123", groups = {"USER"}
    )
})

public class SecurityConfig {
    /*
     * Las anotaciones definen usuarios y roles que el contenedor registra durante el despliegue.
     */
}
