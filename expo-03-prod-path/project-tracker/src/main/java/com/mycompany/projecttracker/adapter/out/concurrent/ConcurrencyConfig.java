package com.mycompany.projecttracker.adapter.out.concurrent;

import jakarta.enterprise.concurrent.ContextServiceDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.context.ApplicationScoped;

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
}
