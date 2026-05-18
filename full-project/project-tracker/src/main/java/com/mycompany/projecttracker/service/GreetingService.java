package com.mycompany.projecttracker.service;

/**
 * Provides greeting text for a caller name.
 */
public interface GreetingService {
    /**
     * Builds a greeting.
     *
     * @param name name to greet
     * @return greeting message
     */
    String greet(String name);
}
