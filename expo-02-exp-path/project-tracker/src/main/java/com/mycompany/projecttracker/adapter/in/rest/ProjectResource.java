package com.mycompany.projecttracker.adapter.in.rest;

import com.mycompany.projecttracker.adapter.in.rest.dto.ProjectDTO;
import com.mycompany.projecttracker.adapter.in.rest.mapper.ProjectRestMapper;
import com.mycompany.projecttracker.application.port.in.ProjectUseCase;
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
    public Response createProject(@Valid ProjectDTO projectRequest) {
        ProjectDTO newProject = mapper.toDTO(
            projectUseCase.create(mapper.toCommand(projectRequest))
        );

        URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(newProject.id())).build();
        return Response.created(location).entity(newProject).build();
    }
}
