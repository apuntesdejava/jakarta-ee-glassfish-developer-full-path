package com.mycompany.projecttracker.demo.cdi;

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
        return "Hello %s !".formatted(name);
    }
}
