package com.mycompany.projecttracker.demo.cdi;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@GreetingType("mock")
public class MockGreeting implements GreetingService {

    @Override
    public String greet(String name) {
        return "Modo de prueba: Hola " + name;
    }
}
