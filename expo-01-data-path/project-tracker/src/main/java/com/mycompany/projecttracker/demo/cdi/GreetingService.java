package com.mycompany.projecttracker.demo.cdi;

/**
 * Contract for greeting implementations selected through CDI qualifiers.
 */
public interface GreetingService {
    /**
     * Builds a greeting message for the provided name.
     *
     * @param name the name to include in the greeting
     * @return formatted greeting text
     */
    String greet(String name);
}
