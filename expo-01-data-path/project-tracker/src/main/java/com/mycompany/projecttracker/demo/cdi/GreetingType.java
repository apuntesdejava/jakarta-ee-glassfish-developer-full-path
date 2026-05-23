package com.mycompany.projecttracker.demo.cdi;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
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
