# Día 3: Operaciones CRUD Básicas con Jakarta Persistence (Antes JPA)

En el Día 2 configuramos Jakarta Persistence y diseñamos nuestras entidades. Hoy, nos centraremos en cómo interactuar con esas entidades en la base de datos: las operaciones **CRUD** (Crear, Leer, Actualizar y Borrar). Para ello, utilizaremos el `EntityManager` de **Jakarta Persistence**.

## 1. El `EntityManager` y su Inyección

El `EntityManager` es la interfaz central en Jakarta Persistence para todas las operaciones de persistencia. En un entorno Jakarta EE, no lo instanciamos directamente; el contenedor (GlassFish en nuestro caso) nos lo proporciona mediante **inyección de dependencia**.

Vamos a crear una clase de servicio simple que se encargue de la lógica de negocio básica para nuestra entidad `Project`.

Crea un nuevo paquete en `src/main/java`, por ejemplo, `com.tuempresa.proyecto.service`. Dentro de este paquete, crea una clase `ProjectService.java`:

```java
package com.tuempresa.proyecto.service;

import com.tuempresa.proyecto.domain.Project;
import jakarta.ejb.Stateless; // Usaremos EJB Stateless para gestionar transacciones
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless // Indica que esta clase es un EJB Stateless, gestionado por el contenedor
public class ProjectService {

    @PersistenceContext(unitName = "pm-pu") // Inyecta el EntityManager para nuestra unidad de persistencia
    private EntityManager em;

    /**
     * Crea un nuevo proyecto en la base de datos.
     * @param project El objeto Project a persistir.
     * @return El proyecto persistido (con ID generado si aplica).
     */
    public Project createProject(Project project) {
        // 'persist' hace que una nueva entidad pase al estado gestionado y se prepare para ser insertada.
        em.persist(project);
        return project; // El ID se asigna al objeto Project después de persistir
    }

    /**
     * Busca un proyecto por su ID.
     * @param id El ID del proyecto.
     * @return El proyecto encontrado o null si no existe.
     */
    public Project findProjectById(Long id) {
        // 'find' busca una entidad por su clave primaria.
        return em.find(Project.class, id);
    }

    /**
     * Actualiza un proyecto existente en la base de datos.
     * @param project El objeto Project con los datos actualizados.
     * @return El proyecto actualizado (puede ser la misma instancia o una gestionada por JPA).
     */
    public Project updateProject(Project project) {
        // 'merge' se usa para entidades que podrían no estar en el contexto de persistencia.
        // Si la entidad ya existe y está gestionada, la sincroniza. Si no, la adjunta.
        return em.merge(project);
    }

    /**
     * Elimina un proyecto de la base de datos.
     * @param id El ID del proyecto a eliminar.
     */
    public void deleteProject(Long id) {
        Project project = findProjectById(id);
        if (project != null) {
            // 'remove' elimina la entidad del contexto de persistencia y la marca para eliminación de la BD.
            em.remove(project);
        }
    }

    /**
     * Obtiene todos los proyectos de la base de datos.
     * @return Una lista de todos los proyectos.
     */
    public List<Project> findAllProjects() {
        // Se utiliza JPQL (Jakarta Persistence Query Language) para consultar entidades.
        // "FROM Project" significa seleccionar todas las instancias de la entidad Project.
        return em.createQuery("SELECT p FROM Project p", Project.class).getResultList();
    }
}
```

**Explicación de las Anotaciones Clave**:

- `@Stateless`: Esta anotación convierte la clase `ProjectService` en un **Enterprise Bean sin estado**. Los Enterprise Bean son componentes de servidor que el contenedor Jakarta EE gestiona. `@Stateless` es ideal para servicios de negocio porque son ligeros y pueden ser reutilizados por múltiples clientes de forma concurrente, sin mantener estado entre llamadas.
- `@PersistenceContext(unitName = "pm-pu")`: Esta es la joya de la inyección. Le dice al contenedor Jakarta EE que inyecte una instancia de `EntityManager` en la variable `em`.
  - `unitName = "pm-pu"`: Es crucial. Indica a Jakarta Persistence qué unidad de persistencia (definida en `persistence.xml`) debe usar para este `EntityManager`. Asegúrate de que el nombre (`pm-pu`) coincida exactamente con el que definiste en tu `persistence.xml` en el Día 2.

## 2. Probando las Operaciones CRUD (Temporalmente con Jakarta RESTful)

Para verificar rápidamente que nuestras operaciones CRUD funcionan, podemos añadir temporalmente algunos endpoints a nuestro recurso Jakarta RESTful existente del Día 1. En los próximos días, construiremos una interfaz web más robusta.

Crea la clase `ProjectResource.java` en la carpeta `com.tuempresa.proyecto.resources`) e inyectamos  `ProjectService` para exponer algunos métodos.

```java
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
    @Path("all") // Nuevo path para obtener todos los proyectos
    public List<Project> getAllProjects() {
        return projectService.findAllProjects();
    }
    
    // Un método simple para crear un proyecto de prueba desde el navegador (GET)
    // ESTO ES SOLO PARA PRUEBAS RÁPIDAS Y DEBE SER ELIMINADO EN UN ENTORNO REAL
    @GET
    @Path("create-test")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTestProject() {
        Project testProject = new Project("Test Project " + System.currentTimeMillis(), "Description for test project", LocalDate.now(), LocalDate.now().plusMonths(1));
        projectService.createProject(testProject);
        return Response.ok(testProject).build();
    }
}
```

**Notas sobre el código Jakarta RESTful**:

- `@Inject`: Para que el contenedor inyecte nuestro `ProjectService` Enterprise Bean en el recurso Jakarta RESTful.
- `@Path("projects")`: Establece el prefijo de la URL para todos los métodos en esta clase (ej. `/rest/projects`).
- `@Consumes(MediaType.APPLICATION_JSON)`: Indica que el método espera un cuerpo de request en formato JSON.
- `@Produces(MediaType.APPLICATION_JSON)`: Indica que el método devolverá una respuesta en formato JSON.
- `Response`: Clase de Jakarta RESTful para construir respuestas HTTP personalizadas (estados, cabeceras, etc.).
- Método `createTestProject`: Es una forma muy rápida de crear un proyecto con solo acceder a una URL desde el navegador. ¡Recuerda eliminar o deshabilitar este método en un entorno real de desarrollo o producción!

## 3. Prueba de la Aplicación

1. Guarda todos los archivos: `ProjectService.java` y `ProjectResource.java`.
2. Re-despliega la aplicación en GlassFish desde tu IDE.
3. Usa una herramienta como **Postman**, **Insomnia**, **HTTPie** o **curl** para probar los endpoints REST. O simplemente tu navegador para los métodos GET.
   - Crear un Proyecto (POST):
     - URL: http://localhost:8080/mi-proyecto-pm/api/projects
     - Método: `POST`
     - Headers: `Content-Type: application/json`
     - Body (raw JSON):
       ```json
          {
             "name": "Mi Primer Proyecto",
             "description": "Un proyecto de ejemplo para el tutorial.",
             "startDate": "2025-07-24",
             "endDate": "2025-08-24"
          }
       ```
     -   Opcional, con navegador: http://localhost:8080/mi-proyecto-pm/api/projects/create-test (varias veces para crear algunos)
   - Obtener Todos los Proyectos (GET):
     - URL: http://localhost:8080/mi-proyecto-pm/api/projects/all
     - Método: `GET`
     - Deberías ver una lista JSON de los proyectos que creaste.
   - Obtener un Proyecto por ID (GET):
     - URL: http://localhost:8080/mi-proyecto-pm/api/projects/{id_del_proyecto} (reemplaza {id_del_proyecto} con un ID real de tu lista)
     - Método: `GET`
   - Actualizar un Proyecto (PUT):
     - URL: http://localhost:8080/mi-proyecto-pm/api/projects/{id_del_proyecto_a_actualizar}
     - Método: `PUT`
     - Headers: `Content-Type: application/json`
     - Body (raw JSON):
       ```json
         {
            "name": "Mi Proyecto Actualizado",
            "description": "La nueva descripción del proyecto.",
            "startDate": "2025-07-25",
            "endDate": "2025-09-25"
          }
       ```
   - Eliminar un Proyecto (DELETE):
     - URL: http://localhost:8080/mi-proyecto-pm/api/projects/{id_del_proyecto_a_eliminar}
     - Método: `DELETE`

Al realizar estas operaciones, el `EntityManager` gestionará las transacciones con la base de datos a través de nuestro Enterprise Bean `ProjectService`.

---

¡Excelente trabajo! Has implementado y probado las operaciones CRUD básicas para tus entidades Jakarta Persistence. Este es el corazón de la persistencia de datos en cualquier aplicación empresarial.

Mañana, en el Día 4, profundizaremos en las Relaciones entre Entidades (`@OneToMany`, `@ManyToOne`, etc.) para conectar nuestros proyectos con tareas, usuarios y otros elementos.

¿Pudiste probar las operaciones CRUD exitosamente con Postman/Insomnia? ¿Alguna duda con los Enterprise Bean o la inyección de `EntityManager`?
 