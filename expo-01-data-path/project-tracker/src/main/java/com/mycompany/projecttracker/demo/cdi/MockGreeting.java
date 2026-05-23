package com.mycompany.projecttracker.demo.cdi;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Mock greeting implementation used to demonstrate CDI qualifier selection.
 */
@ApplicationScoped
@GreetingType("mock")
public class MockGreeting implements GreetingService {
    /**
     * Builds a test-oriented greeting message.
     *
     * @param name the name to include in the message
     * @return formatted mock greeting text
     */
    @Override
    public String greet(String name) {
        return "Modo de prueba: Hola " + name;
    }
}
