package com.mycompany.projecttracker.rest.mapper;

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

        // Cada violación se reduce a un mensaje simple para no exponer detalles internos del runtime.
        List<String> errors = exception.getConstraintViolations().stream()
            .map(this::formatError)
            .toList();

        // El cuerpo mantiene un mensaje general y una lista concreta para consumo humano o automatizado.
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

        // El path puede venir como "createProject.arg0.name"; para el cliente basta el último segmento.
        String[] parts = field.split("\\.");
        if (parts.length > 0) {
            field = parts[parts.length - 1];
        }

        return field + ": " + message;
    }
}
