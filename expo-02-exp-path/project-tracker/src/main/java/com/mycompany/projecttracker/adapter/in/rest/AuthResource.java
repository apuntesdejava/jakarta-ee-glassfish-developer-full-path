package com.mycompany.projecttracker.adapter.in.rest;

import com.mycompany.projecttracker.adapter.in.security.TokenService;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Map;

/**
 * REST adapter that authenticates API clients and issues JWT bearer tokens.
 */
@Path("/auth")
public class AuthResource {

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Inject
    private TokenService tokenService;

    public record LoginRequest(@NotNull String username, @NotNull String password) {}

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@Valid LoginRequest request) {
        CredentialValidationResult result = identityStoreHandler.validate(
            new UsernamePasswordCredential(request.username, request.password)
        );

        if (result.getStatus() == CredentialValidationResult.Status.VALID) {
            String token = tokenService.generateToken(result.getCallerPrincipal().getName(), result.getCallerGroups());
            return Response.ok(Map.of("token", token)).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
