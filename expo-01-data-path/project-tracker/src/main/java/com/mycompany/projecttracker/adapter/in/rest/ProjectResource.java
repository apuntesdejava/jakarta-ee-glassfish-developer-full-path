package com.mycompany.projecttracker.adapter.in.rest;

import com.mycompany.projecttracker.adapter.in.rest.dto.ProjectDTO;
import com.mycompany.projecttracker.adapter.in.rest.mapper.ProjectRestMapper;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
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
 * REST adapter that exposes project operations over HTTP.
 */
@Path("/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectResource {

    /**
     * Input port that owns project use cases.
     */
    @Inject
    private ProjectUseCase projectUseCase;

    /**
     * Mapper that translates between REST DTOs and application models.
     */
    @Inject
    private ProjectRestMapper mapper;

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

    /**
     * Returns a single project by its identifier.
     *
     * @param id the project identifier
     * @return HTTP 200 with the project, or HTTP 404 when it does not exist
     */
    @GET
    @Path("/{id}")
    public Response getProjectById(@PathParam("id") Long id) {
        return projectUseCase.findById(id)
            .map(mapper::toDTO)
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
    public Response createProject(@Valid ProjectDTO projectRequest) {
        ProjectDTO newProject = mapper.toDTO(
            projectUseCase.create(mapper.toCommand(projectRequest))
        );

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newProject.id())).build();
        return Response.created(location).entity(newProject).build();
    }
}
