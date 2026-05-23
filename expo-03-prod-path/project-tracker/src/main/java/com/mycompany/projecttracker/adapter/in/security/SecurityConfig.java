package com.mycompany.projecttracker.adapter.in.security;

import jakarta.annotation.security.DeclareRoles;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.annotation.FacesConfig;
import jakarta.security.enterprise.identitystore.InMemoryIdentityStoreDefinition;

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
}
