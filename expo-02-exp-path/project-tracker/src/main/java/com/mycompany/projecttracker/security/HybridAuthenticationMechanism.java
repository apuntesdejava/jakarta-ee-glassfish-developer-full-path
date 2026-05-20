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
 * Authentication mechanism that supports stateless JWT for REST and session login for JSF.
 */
@ApplicationScoped
@AutoApplySession
public class HybridAuthenticationMechanism implements HttpAuthenticationMechanism {

    /**
     * Service used to validate and inspect JWT bearer tokens.
     */
    @Inject
    private TokenService tokenService;

    /**
     * Jakarta Security identity store bridge used for username/password validation.
     */
    @Inject
    private IdentityStoreHandler identityStoreHandler;

    /**
     * Authenticates incoming requests according to the target surface.
     *
     * @param request current servlet request
     * @param response current servlet response
     * @param context Jakarta Security message context
     * @return authentication status for the container
     * @throws AuthenticationException when the mechanism cannot complete authentication
     */
    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthenticationException {

        String path = request.getRequestURI();

        // La API REST usa Bearer token para no depender de la sesión del navegador.
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
            // Sin token, JAX-RS decide según las anotaciones de seguridad del endpoint.
            return context.doNothing();
        }

        // La UI JSF usa autenticación con estado para aprovechar sesión y cookies.
        Credential credential = context.getAuthParameters().getCredential();
        if (credential != null) {
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            return context.notifyContainerAboutLogin(result);
        }

        boolean isLoginPage = path.contains("login.xhtml");
        boolean isFacelet = path.endsWith(".xhtml");
        boolean isRoot = path.endsWith("/");

        // Las páginas JSF quedan protegidas, excepto login.xhtml.
        if ((isFacelet || isRoot) && !isLoginPage && request.getUserPrincipal() == null) {
            try {
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
                return AuthenticationStatus.SEND_CONTINUE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Si ya hay identidad o el recurso es público/estático, el request continúa.
        return context.doNothing();
    }
}
