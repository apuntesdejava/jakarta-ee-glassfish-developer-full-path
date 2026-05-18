package com.mycompany.projecttracker.rest.mapper;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.List;
import java.util.Map;

/**
 * Converts Jakarta Validation exceptions into HTTP 400 responses.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    /**
     * Builds a bad request response from constraint violations.
     *
     * @param exception validation exception raised by Jakarta Validation
     * @return HTTP 400 response with a validation error body
     */
    @Override
    public Response toResponse(ConstraintViolationException exception) {

        /*
         * Cada violación se compacta en un mensaje legible para que el cliente pueda mostrar los
         * errores cerca de los campos correspondientes.
         */
        List<String> errors = exception.getConstraintViolations().stream()
            .map(this::formatError)
            .toList();

        /*
         * El cuerpo mantiene un mensaje general y la lista detallada de errores de validación.
         */
        Map<String, Object> responseBody = Map.of(
            "message", "La petición tiene errores de validación",
            "errors", errors
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(responseBody)
            .build();
    }

    /**
     * Formats a constraint violation as {@code field: message}.
     *
     * @param violation constraint violation to format
     * @return readable validation error
     */
    private String formatError(ConstraintViolation<?> violation) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        /*
         * Algunas rutas incluyen método y argumento; se conserva solo el último segmento para
         * entregar un nombre de campo simple.
         */
        String[] parts = field.split("\\.");
        if (parts.length > 0) {
            field = parts[parts.length - 1];
        }

        return field + ": " + message;
    }
}
