package com.mycompany.projecttracker.adapter.in.rest.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

/**
 * JAX-RS exception mapper that turns Jakarta Validation failures into HTTP 400 responses.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * Builds a client-friendly response body for validation errors.
     *
     * @param exception the validation exception raised by Jakarta Validation
     * @return HTTP 400 response with a compact list of validation messages
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations().stream()
            .map(this::formatError)
            .toList();

        Map<String, Object> responseBody = Map.of(
            "message", "La petición tiene errores de validación",
            "errors", errors
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(responseBody)
            .build();
    }

    /**
     * Formats a single constraint violation as {@code field: message}.
     *
     * @param violation the constraint violation to format
     * @return a concise validation message
     */
    private String formatError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        String[] parts = field.split("\\.");
        if (parts.length > 0) {
            field = parts[parts.length - 1];
        }

        return field + ": " + message;
    }
}
