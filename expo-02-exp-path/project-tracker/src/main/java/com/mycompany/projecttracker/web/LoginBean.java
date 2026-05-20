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
 * JSF backing bean that performs form-based login through Jakarta Security.
 */
@Named
@RequestScoped
public class LoginBean {

    /**
     * Jakarta Security context used to start authentication.
     */
    @Inject
    private SecurityContext securityContext;

    /**
     * Current Faces context used to access the servlet request and response.
     */
    @Inject
    private FacesContext facesContext;

    /**
     * Username entered in the login form.
     */
    private String username;

    /**
     * Password entered in the login form.
     */
    private String password;

    /**
     * Authenticates the submitted credentials and navigates to the project page.
     *
     * @return JSF navigation outcome
     */
    public String login() {
        ExternalContext externalContext = facesContext.getExternalContext();

        // El bean no valida credenciales directamente; delega en el mecanismo Jakarta Security.
        AuthenticationStatus status = securityContext.authenticate(
            (HttpServletRequest) externalContext.getRequest(),
            (HttpServletResponse) externalContext.getResponse(),
            AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(username, password))
        );

        if (status == AuthenticationStatus.SUCCESS) {
            return "index.xhtml?faces-redirect=true";
        } else {
            // Faces mantiene el mensaje asociado al request para renderizarlo en la vista de login.
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fallido", null));
            return null;
        }
    }

    /**
     * Returns the entered username.
     *
     * @return username entered in the form
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the entered username.
     *
     * @param username username entered in the form
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the entered password.
     *
     * @return password entered in the form
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the entered password.
     *
     * @param password password entered in the form
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
