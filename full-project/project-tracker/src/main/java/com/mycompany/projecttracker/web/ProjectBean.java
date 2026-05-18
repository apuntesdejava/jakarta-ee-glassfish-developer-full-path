package com.mycompany.projecttracker.web;

import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.service.ProjectService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.List;

/**
 * Request-scoped JSF backing bean for the project dashboard.
 */
@Named
@RequestScoped
public class ProjectBean {

    /** Service used to load and create projects. */
    @Inject
    private ProjectService projectService;

    /** Projects displayed by the JSF table. */
    private List<ProjectDTO> projects;

    /** Project DTO used by the simple creation flow. */
    private ProjectDTO newProject = new ProjectDTO(null, null, null, null);

    /**
     * Initializes the dashboard data after dependency injection.
     */
    @PostConstruct
    public void init() {
        loadProjects();
    }

    /**
     * Loads the current project list for the view.
     */
    private void loadProjects() {
        this.projects = projectService.findAll();
    }

    /**
     * Creates a project from {@link #newProject}.
     *
     * @return JSF navigation outcome that keeps the user on the current page
     */
    public String createProject() {
        /*
         * Se guarda el DTO actual, luego se limpia el formulario y se refresca la tabla visible.
         */
        projectService.create(newProject);

        this.newProject = new ProjectDTO(null, null, null, null);

        loadProjects();

        return "";
    }

    /** @return projects displayed by the table */
    public List<ProjectDTO> getProjects() {
        return projects;
    }

    /** @return project DTO used by the creation flow */
    public ProjectDTO getNewProject() {
        return newProject;
    }

    /** @param newProject project DTO used by the creation flow */
    public void setNewProject(ProjectDTO newProject) {
        this.newProject = newProject;
    }

    /** Mutable project name field bound directly from JSF inputs. */
    private String formName;

    /** Mutable project description field bound directly from JSF inputs. */
    private String formDescription;

    /** @return project name typed in the form */
    public String getFormName() { return formName; }
    /** @param formName project name typed in the form */
    public void setFormName(String formName) { this.formName = formName; }
    /** @return project description typed in the form */
    public String getFormDescription() { return formDescription; }
    /** @param formDescription project description typed in the form */
    public void setFormDescription(String formDescription) { this.formDescription = formDescription; }

    /**
     * Creates a project from the mutable form fields.
     *
     * @return JSF navigation outcome that keeps the user on the current page
     */
    public String createProjectFromForm() {
        /*
         * JSF escribe en campos mutables y el bean construye el record inmutable justo antes de
         * llamar al servicio.
         */
        ProjectDTO dto = new ProjectDTO(null, formName, formDescription, null);
        projectService.create(dto);

        formName = "";
        formDescription = "";

        loadProjects();
        return "";
    }
}
