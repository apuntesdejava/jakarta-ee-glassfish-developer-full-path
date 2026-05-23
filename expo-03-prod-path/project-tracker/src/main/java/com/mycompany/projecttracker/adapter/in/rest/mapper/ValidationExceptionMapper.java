package com.mycompany.projecttracker.adapter.in.rest.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

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
