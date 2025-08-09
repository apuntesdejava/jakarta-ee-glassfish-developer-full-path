package com.tuempresa.proyecto.resources;

import com.tuempresa.proyecto.domain.Project;
import com.tuempresa.proyecto.service.ProjectService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.inject.Inject; // Importante: para inyectar EJBs en recursos JAX-RS
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate; // Asegúrate de importar LocalDate
import java.util.List;

@Path("projects") // Cambiamos el path base a 'projects'
public class ProjectResource {

    @Inject // Inyectamos nuestro ProjectService
    private ProjectService projectService;


    @POST
    @Consumes(MediaType.APPLICATION_JSON) // Espera un Project en formato JSON
    @Produces(MediaType.APPLICATION_JSON) // Devuelve el Project creado en JSON
    public Response createProject(Project project) {
        Project createdProject = projectService.createProject(project);
        return Response.status(Response.Status.CREATED).entity(createdProject).build();
    }

    @GET
    @Path("{id}") // Define un path con un parámetro de ID
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectById(@PathParam("id") Long id) {
        Project project = projectService.findProjectById(id);
        if (project != null) {
            return Response.ok(project).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") Long id, Project updatedProject) {
        Project existingProject = projectService.findProjectById(id);
        if (existingProject != null) {
            // Actualizar campos manualmente para evitar problemas con entidades desatachadas
            existingProject.setName(updatedProject.getName());
            existingProject.setDescription(updatedProject.getDescription());
            existingProject.setStartDate(updatedProject.getStartDate());
            existingProject.setEndDate(updatedProject.getEndDate());

            Project result = projectService.updateProject(existingProject);
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteProject(@PathParam("id") Long id) {
        projectService.deleteProject(id);
        return Response.status(Response.Status.NO_CONTENT).build(); // 204 No Content para eliminación exitosa
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> getAllProjects() {
        return projectService.findAllProjects();
    }

    // Un método simple para crear un proyecto de prueba desde el navegador (GET)
    // ESTO ES SOLO PARA PRUEBAS RÁPIDAS Y DEBE SER ELIMINADO EN UN ENTORNO REAL
    @GET
    @Path("create-test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTestProject() {
        Project testProject = new Project("Test Project " + System.currentTimeMillis(), "Description for test project",
            LocalDate.now(), LocalDate.now().plusMonths(1));
        projectService.createProject(testProject);
        return Response.ok(testProject).build();
    }
}