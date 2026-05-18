package com.mycompany.projecttracker.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ContextServiceDefinition;

/**
 * Declares the managed concurrency resources used by the application.
 */
@ApplicationScoped
@ManagedExecutorDefinition(
    name = "java:app/concurrent/VirtualExecutor",
    virtual = true,
    maxAsync = 10,
    context = "java:app/concurrent/MyContext"
)
@ContextServiceDefinition(
    name = "java:app/concurrent/MyContext",
    propagated = {ContextServiceDefinition.SECURITY, ContextServiceDefinition.APPLICATION}
)
public class ConcurrencyConfig {
    /*
     * La clase no necesita métodos: las anotaciones superiores registran el ejecutor virtual y
     * el servicio de contexto cuando el servidor despliega la aplicación.
     */
}
