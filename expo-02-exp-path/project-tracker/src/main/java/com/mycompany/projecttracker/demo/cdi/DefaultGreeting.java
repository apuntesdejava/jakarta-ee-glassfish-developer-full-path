package com.mycompany.projecttracker.demo.cdi;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Default greeting implementation used by the regular hello endpoint.
 */
@ApplicationScoped
@GreetingType("default")
public class DefaultGreeting implements GreetingService {

    @Override
    public String greet(String name) {
        return "Hello %s !".formatted(name);
    }
}
