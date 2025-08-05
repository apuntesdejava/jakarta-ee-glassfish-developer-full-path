package com.tuempresa.proyecto.provider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaProvider {

    @Produces
    @PersistenceContext(name = "pm-pu")
    private EntityManager em;
}
