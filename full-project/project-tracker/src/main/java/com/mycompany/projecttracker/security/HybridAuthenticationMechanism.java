package com.mycompany.projecttracker.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AutoApplySession;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * Authentication mechanism that supports JWT APIs and JSF session login.
 */
@ApplicationScoped
@AutoApplySession
public class HybridAuthenticationMechanism implements HttpAuthenticationMechanism {

    /** Service used to validate and inspect bearer tokens. */
    @Inject
    private TokenService tokenService;

    /** Identity store handler used for username and password validation. */
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    /**
     * Validates incoming HTTP requests for REST and JSF entry points.
     *
     * @param request current servlet request
     * @param response current servlet response
     * @param context Jakarta Security message context
     * @return authentication status for the container
     * @throws AuthenticationException when the security infrastructure fails
     */
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthenticationException {

        String path = request.getRequestURI();

        /*
         * Las rutas REST usan JWT sin sesión de servidor: si existe un Bearer token válido, se
         * informa la identidad al contenedor.
         */
        if (path.contains("/resources/")) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = tokenService.validateTokenAndGetUser(token);
                    Set<String> roles = tokenService.getRoles(token);
                    return context.notifyContainerAboutLogin(username, roles);
                } catch (Exception e) {
                    return context.responseUnauthorized();
                }
            }
            /*
             * Si no llega token, JAX-RS decide después si el endpoint permite anónimos o exige
             * roles.
             */
            return context.doNothing();
        }

        /*
         * Las páginas JSF usan sesión: cuando el LoginBean envía credenciales, se validan y se
         * registra el resultado en el contenedor.
         */
        Credential credential = context.getAuthParameters().getCredential();
        if (credential != null) {
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            return context.notifyContainerAboutLogin(result);
        }

        /*
         * Las vistas XHTML quedan protegidas salvo la pantalla de login y los recursos públicos.
         */
        boolean isLoginPage = path.contains("login.xhtml");
        boolean isFacelet = path.endsWith(".xhtml");

        boolean isRoot = path.endsWith("/");

        if ((isFacelet || isRoot) && !isLoginPage && request.getUserPrincipal() == null) {
            try {
                /*
                 * Un usuario sin sesión se redirige al formulario para iniciar autenticación
                 * stateful.
                 */
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
                return AuthenticationStatus.SEND_CONTINUE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return context.doNothing();
    }
}
