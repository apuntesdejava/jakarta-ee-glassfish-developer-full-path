package com.mycompany.projecttracker.demo.cdi;

/**
 * Contract for greeting implementations selected through CDI qualifiers.
 */
public interface GreetingService {
    String greet(String name);
}
