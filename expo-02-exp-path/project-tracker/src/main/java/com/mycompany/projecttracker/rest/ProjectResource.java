package com.mycompany.projecttracker.rest;

import com.mycompany.projecttracker.model.ProjectDTO;
import com.mycompany.projecttracker.service.ProjectService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
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

import java.net.URI;
import java.util.List;

/**
 * REST resource that exposes project operations over HTTP.
 */
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    /**
     * Application service that owns project business logic.
     */
    @Inject
    private ProjectService projectService;

    /**
     * Request URI information used to build Location headers.
     */
    @Context
    private UriInfo uriInfo;

    /**
     * Returns all projects, optionally filtered by status.
     *
     * @param status optional lifecycle status filter
     * @return HTTP response containing the matching projects
     */
    @GET
    @PermitAll
    public Response getProjects(@QueryParam("status") String status) {
        List<ProjectDTO> projects;

        // Si llega un filtro, la consulta se delega al método derivado de Jakarta Data.
        if (status != null && !status.isBlank()) {
            projects = projectService.findByStatus(status);
        } else {
            projects = projectService.findAll();
        }

        return Response.ok(projects).build();
    }

    /**
     * Returns a single project by its identifier.
     *
     * @param id the project identifier
     * @return HTTP 200 with the project, or HTTP 404 when it does not exist
     */
    @GET
    @Path("/{id}")
    @PermitAll
    public Response getProjectById(@PathParam("id") Long id) {
        // Optional permite expresar el caso "no encontrado" sin condicionales auxiliares.
        return projectService.findById(id)
            .map(project -> Response.ok(project).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    /**
     * Creates a project from a validated request body.
     *
     * @param projectRequest validated project payload
     * @return HTTP 201 with the created project and its Location header
     */
    @POST
    @RolesAllowed("ADMIN")
    public Response createProject(@Valid ProjectDTO projectRequest) {
        // Jakarta Validation ya verificó las reglas declaradas en ProjectDTO antes de entrar aquí.
        ProjectDTO newProject = projectService.create(projectRequest);

        // La URI del recurso creado queda disponible para clientes REST bien comportados.
        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newProject.id())).build();
        return Response.created(location).entity(newProject).build();
    }
}
