package com.mycompany.projecttracker.adapter.in.rest;

import com.mycompany.projecttracker.application.port.in.ReportUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

@Path("/reports")
public class ReportResource {

    @Inject
    private ReportUseCase reportUseCase;

    @POST
    @Path("/{projectId}")
    @RolesAllowed({"ADMIN", "USER"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestReport(@PathParam("projectId") Long projectId, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        reportUseCase.generateReportAsync(projectId, username);

        var response = Json.createObjectBuilder()
            .add("status", "Reporte solicitado. Procesando en segundo plano.")
            .build();

        return Response.accepted()
            .entity(response)
            .build();
    }
}
