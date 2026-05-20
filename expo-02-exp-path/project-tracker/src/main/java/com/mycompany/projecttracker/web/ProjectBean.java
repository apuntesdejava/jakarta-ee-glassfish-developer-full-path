package com.mycompany.projecttracker.web;

import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.service.ProjectService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 * JSF backing bean that supports the project listing and creation view.
 */
@Named
@RequestScoped
public class ProjectBean {

    /**
     * Application service used by the view actions.
     */
    @Inject
    private ProjectService projectService;

    /**
     * Projects rendered in the JSF table.
     */
    private List<ProjectDTO> projects;

    /**
     * Project DTO bound to the creation form.
     */
    private ProjectDTO newProject = new ProjectDTO(null, null, null, null);

    /**
     * Initializes the view model after CDI injection.
     */
    @PostConstruct
    public void init() {
        loadProjects();
    }

    /**
     * Loads the latest projects from the application service.
     */
    private void loadProjects() {
        this.projects = projectService.findAll();
    }

    /**
     * Creates a project using the DTO bound to the form.
     *
     * @return JSF navigation outcome that keeps the user on the same page
     */
    public String createProject() {
        projectService.create(newProject);

        // Después de guardar, el formulario vuelve a quedar listo para una nueva captura.
        this.newProject = new ProjectDTO(null, null, null, null);

        // La lista se recarga para reflejar el cambio sin depender de estado local.
        loadProjects();

        return "";
    }

    /**
     * Returns projects rendered by the view.
     *
     * @return current project list
     */
    public List<ProjectDTO> getProjects() {
        return projects;
    }

    /**
     * Returns the DTO bound to the creation form.
     *
     * @return project creation DTO
     */
    public ProjectDTO getNewProject() {
        return newProject;
    }

    /**
     * Updates the DTO bound to the creation form.
     *
     * @param newProject project creation DTO
     */
    public void setNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
    }

    /**
     * Mutable form field used because records do not expose setters for JSF binding.
     */
    private String formName;

    /**
     * Mutable form description used to build the immutable DTO.
     */
    private String formDescription;

    /**
     * Returns the project name entered in the form.
     *
     * @return form project name
     */
    public String getFormName() { return formName; }

    /**
     * Updates the project name entered in the form.
     *
     * @param formName form project name
     */
    public void setFormName(String formName) { this.formName = formName; }

    /**
     * Returns the project description entered in the form.
     *
     * @return form project description
     */
    public String getFormDescription() { return formDescription; }

    /**
     * Updates the project description entered in the form.
     *
     * @param formDescription form project description
     */
    public void setFormDescription(String formDescription) { this.formDescription = formDescription; }

    /**
     * Creates a project from mutable JSF form fields.
     *
     * @return JSF navigation outcome that keeps the user on the same page
     */
    public String createProjectFromForm() {
        // Se construye el record justo antes de llamar al servicio para mantener inmutable el contrato.
        ProjectDTO dto = new ProjectDTO(null, formName, formDescription, null);
        projectService.create(dto);

        // Limpiar los campos evita que JSF vuelva a mostrar valores ya persistidos.
        formName = "";
        formDescription = "";

        loadProjects();
        return "";
    }
}
