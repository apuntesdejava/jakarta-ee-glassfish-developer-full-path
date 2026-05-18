package com.mycompany.projecttracker.service.impl;

import com.mycompany.projecttracker.service.GreetingService;
import com.mycompany.projecttracker.service.qualifier.GreetingType;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Default greeting implementation for normal application flow.
 */
@ApplicationScoped
@GreetingType("default")
public class DefaultGreeting implements GreetingService {
    /**
     * Builds the default greeting message.
     *
     * @param name name to greet
     * @return formatted greeting
     */
    @Override
    public String greet(String name) {
        return "Hello %s !".formatted(name);
    }
}
