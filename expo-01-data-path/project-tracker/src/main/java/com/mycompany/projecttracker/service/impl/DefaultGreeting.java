package com.mycompany.projecttracker.service.impl;

import com.mycompany.projecttracker.service.GreetingService;
import com.mycompany.projecttracker.service.qualifier.GreetingType;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Default greeting implementation used by the regular hello endpoint.
 */
@ApplicationScoped
@GreetingType("default")
public class DefaultGreeting implements GreetingService {
    /**
     * Builds the default English greeting.
     *
     * @param name the name to include in the message
     * @return formatted greeting text
     */
    @Override
    public String greet(String name) {
        // Esta implementación representa el bean principal elegido por el qualifier "default".
        return "Hello %s !".formatted(name);
    }
}
