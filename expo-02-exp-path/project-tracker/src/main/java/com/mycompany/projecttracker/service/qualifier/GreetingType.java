package com.mycompany.projecttracker.service.qualifier;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier used to select a specific greeting implementation.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface GreetingType {
    /**
     * Identifies the greeting implementation to inject.
     *
     * @return the implementation name
     */
    String value() default "default";
}
