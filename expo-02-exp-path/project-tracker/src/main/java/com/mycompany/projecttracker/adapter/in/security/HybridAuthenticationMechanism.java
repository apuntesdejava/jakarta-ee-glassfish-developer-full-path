package com.mycompany.projecttracker.adapter.in.security;

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

    @Inject
    private TokenService tokenService;

    @Inject
    private IdentityStoreHandler identityStoreHandler;

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext context) throws AuthenticationException {
        String path = request.getRequestURI();

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
            return context.doNothing();
        }

        Credential credential = context.getAuthParameters().getCredential();
        if (credential != null) {
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            return context.notifyContainerAboutLogin(result);
        }

        boolean isLoginPage = path.contains("login.xhtml");
        boolean isFacelet = path.endsWith(".xhtml");
        boolean isRoot = path.endsWith("/");

        if ((isFacelet || isRoot) && !isLoginPage && request.getUserPrincipal() == null) {
            try {
                response.sendRedirect(request.getContextPath() + "/login.xhtml");
                return AuthenticationStatus.SEND_CONTINUE;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return context.doNothing();
    }
}
