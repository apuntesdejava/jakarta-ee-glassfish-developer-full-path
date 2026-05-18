package com.mycompany.projecttracker.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Request-scoped JSF backing bean for login form submissions.
 */
@Named
@RequestScoped
public class LoginBean {

    /** Jakarta Security context used to authenticate the submitted credentials. */
    @Inject
    private SecurityContext securityContext;

    /** Current Faces context used to access request objects and messages. */
    @Inject
    private FacesContext facesContext;

    /** Username submitted by the login form. */
    private String username;

    /** Password submitted by the login form. */
    private String password;

    /**
     * Authenticates the user and navigates to the index page on success.
     *
     * @return JSF navigation outcome
     */
    public String login() {
        ExternalContext externalContext = facesContext.getExternalContext();

        /*
         * El bean envía las credenciales al mecanismo HTTP para que el contenedor cree la sesión
         * autenticada.
         */
        AuthenticationStatus status = securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(username, password))
        );

        if (status == AuthenticationStatus.SUCCESS) {
            return "index.xhtml?faces-redirect=true";
        } else {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fallido", null));
            return null;
        }
    }

    /** @return username submitted by the login form */
    public String getUsername() {
        return username;
    }

    /** @param username username submitted by the login form */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return password submitted by the login form */
    public String getPassword() {
        return password;
    }

    /** @param password password submitted by the login form */
    public void setPassword(String password) {
        this.password = password;
    }
}
