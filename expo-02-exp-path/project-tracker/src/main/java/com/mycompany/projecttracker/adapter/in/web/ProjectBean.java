package com.mycompany.projecttracker.adapter.in.web;

import com.mycompany.projecttracker.application.command.CreateProjectCommand;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
import com.mycompany.projecttracker.application.result.ProjectResult;
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

    @Inject
    private ProjectUseCase projectUseCase;

    private List<ProjectResult> projects;

    private String formName;

    private String formDescription;

    @PostConstruct
    public void init() {
        loadProjects();
    }

    private void loadProjects() {
        this.projects = projectUseCase.findAll();
    }

    public List<ProjectResult> getProjects() {
        return projects;
    }

    public String getFormName() { return formName; }

    public void setFormName(String formName) { this.formName = formName; }

    public String getFormDescription() { return formDescription; }

    public void setFormDescription(String formDescription) { this.formDescription = formDescription; }

    public String createProjectFromForm() {
        projectUseCase.create(new CreateProjectCommand(formName, formDescription, null));

        formName = "";
        formDescription = "";

        loadProjects();
        return "";
    }
}
