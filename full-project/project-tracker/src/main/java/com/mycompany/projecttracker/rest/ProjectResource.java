package com.mycompany.projecttracker.rest;

import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.model.TaskDTO;
import com.mycompany.projecttracker.service.ProjectService;
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
 * REST resource that exposes project and task operations.
 */
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    /** Service that contains the project business logic. */
    @Inject
    private ProjectService projectService;

    /** URI information used to build resource locations. */
    @Context
    private UriInfo uriInfo;

    /**
     * Lists projects, optionally filtered by status.
     *
     * @param status optional status filter
     * @return HTTP response with matching projects
     */
    @GET
    @PermitAll
    @Counted(name = "getAllProjects_total", description = "Total de veces que se listaron los proyectos")
    @Timed(name = "getAllProjects_timer", description = "Tiempo de respuesta de listado", unit = "milliseconds")
    public Response getProjects(@QueryParam("status") String status) {
        List<ProjectDTO> projects;

        /*
         * Si el cliente envía un estado, se delega al método de consulta derivado; en caso
         * contrario se devuelve el listado completo.
         */
        if (status != null && !status.isBlank()) {
            projects = projectService.findByStatus(status);
        } else {
            projects = projectService.findAll();
        }

        return Response.ok(projects).build();
    }

    /**
     * Gets a project by identifier.
     *
     * @param id project identifier
     * @return HTTP 200 with the project or HTTP 404 when it does not exist
     */
    @GET
    @Path("/{id}")
    @PermitAll
    public Response getProjectById(@PathParam("id") Long id) {
        return projectService.findById(id)
            .map(project -> Response.ok(project).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Creates a project.
     *
     * @param projectRequest project data to persist
     * @return HTTP 201 with the created project and Location header
     */
    @POST
    @RolesAllowed("ADMIN")
    @Counted(name = "createProject_total", description = "Total de proyectos creados")
    public Response createProject(@Valid ProjectDTO projectRequest) {

        ProjectDTO newProject = projectService.create(projectRequest);
        /*
         * La URL absoluta del nuevo recurso se expone en Location para que el cliente pueda
         * consultarlo directamente después de crearlo.
         */
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newProject.id())).build();
        return Response.created(location).entity(newProject).build();
    }

    /**
     * Creates a task inside a project.
     *
     * @param projectId owning project identifier
     * @param taskDto task data to persist
     * @return HTTP 200 with the created task or HTTP 404 when the project is missing
     */
    @POST
    @Path("/{id}/tasks")
    @RolesAllowed({"ADMIN", "USER"})
    public Response createTask(@PathParam("id") Long projectId, @Valid TaskDTO taskDto) {
        try {
            TaskDTO createdTask = projectService.createTask(projectId, taskDto);
            return Response.ok(createdTask).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    /**
     * Starts the task import batch job.
     *
     * @return HTTP 202 with the batch execution identifier
     */
    @POST
    @Path("/import")
    @RolesAllowed("ADMIN")
    public Response runImport() {
        /*
         * El contenedor proporciona el operador de Batch y ejecuta el job declarado en XML.
         */
        JobOperator jobOperator = BatchRuntime.getJobOperator();

        long executionId = jobOperator.start("taskImportJob", new Properties());

        return Response.accepted()
            .entity("Job de importación iniciado con Execution ID: " + executionId)
            .build();
    }
}
