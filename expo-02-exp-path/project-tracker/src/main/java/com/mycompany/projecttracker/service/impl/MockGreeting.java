package com.mycompany.projecttracker.service.impl;

import com.mycompany.projecttracker.service.GreetingService;
import com.mycompany.projecttracker.service.qualifier.GreetingType;
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
        // Esta implementación alternativa permite comprobar que CDI inyecta el bean correcto.
        return "Modo de prueba: Hola " + name;
    }
}
