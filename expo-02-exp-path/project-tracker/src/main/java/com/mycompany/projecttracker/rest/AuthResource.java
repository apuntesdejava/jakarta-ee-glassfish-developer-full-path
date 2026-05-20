package com.mycompany.projecttracker.rest;

import com.mycompany.projecttracker.security.TokenService;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Map;

/**
 * REST resource that authenticates API clients and issues JWT bearer tokens.
 */
@Path("/auth")
public class AuthResource {

    /**
     * Jakarta Security handler that validates credentials against configured identity stores.
     */
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    /**
     * Service responsible for creating signed JWT tokens.
     */
    @Inject
    private TokenService tokenService;

    /**
     * Login payload accepted by the authentication endpoint.
     *
     * @param username the caller name
     * @param password the caller password
     */
    public record LoginRequest(@NotNull String username, @NotNull String password) {}

    /**
     * Validates credentials and returns a bearer token for API calls.
     *
     * @param request validated login payload
     * @return HTTP 200 with a token, or HTTP 401 when credentials are invalid
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest request) {
        // La validación se delega al IdentityStore configurado para mantener una sola fuente de usuarios.
        CredentialValidationResult result = identityStoreHandler.validate(
            new UsernamePasswordCredential(request.username, request.password)
        );

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            // El token transporta el usuario y los grupos para proteger endpoints REST sin sesión web.
            String token = tokenService.generateToken(result.getCallerPrincipal().getName(), result.getCallerGroups());
            return Response.ok(Map.of("token", token)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
