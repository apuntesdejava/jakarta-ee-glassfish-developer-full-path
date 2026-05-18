package com.mycompany.projecttracker.rest;

import com.mycompany.projecttracker.service.ReportService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

/**
 * REST resource for requesting asynchronous reports.
 */
@Path("/reports")
public class ReportResource {

    /** Service that runs report generation work. */
    @Inject
    private ReportService reportService;

    /**
     * Requests report generation for a project.
     *
     * @param projectId project identifier
     * @param securityContext current caller security context
     * @return HTTP 202 when the report request is accepted
     */
    @POST
    @Path("/{projectId}")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestReport(@PathParam("projectId") Long projectId, @Context SecurityContext securityContext) {

        String username = securityContext.getUserPrincipal().getName();

        /*
         * La generación se dispara en segundo plano para que la petición HTTP termine de forma
         * inmediata.
         */
        reportService.generateReportAsync(projectId, username);

        /*
         * La respuesta confirma la aceptación del trabajo sin prometer que ya terminó.
         */
        var response = Json.createObjectBuilder()
            .add("status","Reporte solicitado. Procesando en segundo plano.")
            .build();
        return Response.accepted()
            .entity(response)
            .build();
    }
}
