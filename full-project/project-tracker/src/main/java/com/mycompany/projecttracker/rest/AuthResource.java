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
 * REST resource that authenticates users and issues JWTs.
 */
@Path("/auth")
public class AuthResource {

    /** Identity store handler used to validate username and password credentials. */
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    /** Service that creates signed JWTs for valid callers. */
    @Inject
    private TokenService tokenService;

    /**
     * Login request payload.
     *
     * @param username caller username
     * @param password caller password
     */
    public record LoginRequest(@NotNull String username, @NotNull String password) {}

    /**
     * Validates credentials and returns a bearer token.
     *
     * @param request login credentials
     * @return HTTP 200 with a token or HTTP 401 when credentials are invalid
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest request) {
        /*
         * Las credenciales se delegan al IdentityStore configurado para mantener una única
         * fuente de verdad para usuarios y roles.
         */
        CredentialValidationResult result = identityStoreHandler.validate(
            new UsernamePasswordCredential(request.username, request.password)
        );

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            /*
             * Cuando la identidad es válida, el JWT transporta el usuario y sus grupos para las
             * llamadas REST posteriores.
             */
            String token = tokenService.generateToken(result.getCallerPrincipal().getName(), result.getCallerGroups());
            return Response.ok(Map.of("token", token)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }
}
