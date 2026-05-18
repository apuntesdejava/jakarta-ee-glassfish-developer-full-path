package com.mycompany.projecttracker.service.impl;

import com.mycompany.projecttracker.service.GreetingService;
import com.mycompany.projecttracker.service.qualifier.GreetingType;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mock greeting implementation used for demonstration endpoints.
 */
@ApplicationScoped
@GreetingType("mock")
public class MockGreeting implements GreetingService {
    /**
     * Builds the mock greeting message.
     *
     * @param name name to greet
     * @return mock greeting
     */
    @Override
    public String greet(String name) {
        return "Modo de prueba: Hola " + name;
    }
}
