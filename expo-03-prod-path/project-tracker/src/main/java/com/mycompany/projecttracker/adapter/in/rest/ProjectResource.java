package com.mycompany.projecttracker.adapter.in.rest;

import com.mycompany.projecttracker.adapter.in.rest.dto.ProjectDTO;
import com.mycompany.projecttracker.adapter.in.rest.dto.TaskDTO;
import com.mycompany.projecttracker.adapter.in.rest.mapper.ProjectRestMapper;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.batch.operations.JobOperator;
import jakarta.batch.runtime.BatchRuntime;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

import java.net.URI;
import java.util.List;
import java.util.Properties;

/**
 * REST adapter that exposes project operations over HTTP.
 */
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    @Inject
    private ProjectUseCase projectUseCase;

    @Inject
    private ProjectRestMapper mapper;

    @Context
    private UriInfo uriInfo;

    @GET
    @PermitAll
    @Counted(name = "getAllProjects_total", description = "Total de veces que se listaron los proyectos")
    @Timed(name = "getAllProjects_timer", description = "Tiempo de respuesta de listado", unit = "milliseconds")
    public Response getProjects(@QueryParam("status") String status) {
        List<ProjectDTO> projects;

        if (status != null && !status.isBlank()) {
            projects = projectUseCase.findByStatus(status).stream()
                .map(mapper::toDTO)
                .toList();
        } else {
            projects = projectUseCase.findAll().stream()
                .map(mapper::toDTO)
                .toList();
        }

        return Response.ok(projects).build();
    }

    @GET
    @Path("/{id}")
    @PermitAll
    public Response getProjectById(@PathParam("id") Long id) {
        return projectUseCase.findById(id)
            .map(mapper::toDTO)
            .map(project -> Response.ok(project).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("ADMIN")
    @Counted(name = "createProject_total", description = "Total de proyectos creados")
    public Response createProject(@Valid ProjectDTO projectRequest) {
        ProjectDTO newProject = mapper.toDTO(
            projectUseCase.create(mapper.toCommand(projectRequest))
        );

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newProject.id())).build();
        return Response.created(location).entity(newProject).build();
    }

    @POST
    @Path("/{id}/tasks")
    @RolesAllowed({"ADMIN", "USER"})
    public Response createTask(@PathParam("id") Long projectId, @Valid TaskDTO taskDto) {
        try {
            TaskDTO createdTask = mapper.toDTO(projectUseCase.createTask(projectId, mapper.toCommand(taskDto)));
            return Response.ok(createdTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/import")
    @RolesAllowed("ADMIN")
    public Response runImport() {
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        long executionId = jobOperator.start("taskImportJob", new Properties());

        return Response.accepted()
            .entity("Job de importación iniciado con Execution ID: " + executionId)
            .build();
    }
}
