package com.mycompany.projecttracker.service.qualifier;

import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * CDI qualifier that selects a greeting implementation.
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, METHOD, PARAMETER, TYPE})
public @interface GreetingType {
    /**
     * Returns the greeting implementation name.
     *
     * @return qualifier value used by CDI resolution
     */
    String value() default "default";
}
