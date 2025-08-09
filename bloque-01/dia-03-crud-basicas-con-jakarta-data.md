# (Bonus) Operaciones CRUD con Jakarta Data (Disponible a partir de Jakarta EE 11)

Mientras que el `EntityManager` nos da un control granular sobre la persistencia, Jakarta Data abstrae gran parte del código repetitivo de las operaciones CRUD y consultas comunes. Se basa en el concepto de Repositorios.

## 1. ¿Qué es Jakarta Data?

Jakarta Data es una API declarativa que permite definir interfaces de repositorio para tus entidades. El proveedor de Jakarta Persistence (en nuestro caso, [EclipseLink](https://projects.eclipse.org/projects/ee4j.eclipselink) en GlassFish) implementa automáticamente estos métodos por ti en tiempo de ejecución. Esto significa que escribes menos código boilerplate y te centras más en la lógica de negocio.

## 2. Configuración (Actualización del `pom.xml`)

Para usar Jakarta Data, necesitamos añadir la dependencia en nuestro `pom.xml`.

Abre tu archivo `pom.xml` y añade la siguiente dependencia dentro de la sección `<dependencies>`:


```xml
<dependency>
    <groupId>jakarta.data</groupId>
    <artifactId>jakarta.data-api</artifactId>
    <version>1.0.1</version>
    <scope>provided</scope>
</dependency>
```

**Nota sobre la versión**: Siempre es buena práctica verificar la última versión estable en [Maven Central](https://central.sonatype.com/artifact/jakarta.data/jakarta.data-api) si estás trabajando en un proyecto real.

## 3. Creando un Repositorio con Jakarta Data

Vamos a crear una interfaz de repositorio para nuestra entidad `Project`.

Crea un nuevo paquete en `src/main/java`, por ejemplo, `com.tuempresa.proyecto.repository`. Dentro de este paquete, crea una interfaz llamada `ProjectRepository.java`:

```java
package com.tuempresa.proyecto.repository;

import com.tuempresa.proyecto.domain.Project;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.Repository;

/**
 * Interfaz de repositorio para la entidad Project, usando Jakarta Data.
 * Extiende CrudRepository para operaciones CRUD básicas.
 */
@Repository // Anotación que marca esta interfaz como un repositorio de Jakarta Data
public interface ProjectRepository extends CrudRepository<Project, Long> {
    // CrudRepository nos proporciona automáticamente:
    // - save(Project entity): para crear o actualizar
    // - findById(Long id): para buscar por ID
    // - findAll(): para obtener todos
    // - delete(Project entity): para eliminar por entidad
    // - deleteById(Long id): para eliminar por ID
    // - count(): para contar el número de entidades
    // ...y muchos más.

    // Además, podemos definir métodos de consulta personalizados que Jakarta Data implementará automáticamente
    // siguiendo patrones de nombres:
    List<Project> findByNameLike(String name); // Encontrará proyectos cuyo nombre contenga 'name'
    List<Project> findByStartDateAfter(java.time.LocalDate date); // Encontrará proyectos que empiezan después de una fecha
    long countByEndDateBefore(java.time.LocalDate date); // Contará proyectos que terminan antes de una fecha
}
```

**Explicación**:

- `@Repository`: Esta anotación de Jakarta Data marca la interfaz como un repositorio gestionado por el contenedor.
- `extends CrudRepository<Project, Long>`: Aquí está la magia. Al extender `CrudRepository`, tu interfaz `ProjectRepository` hereda automáticamente un conjunto de métodos para todas las operaciones CRUD básicas.
    - `Project`: Es el tipo de entidad con el que trabajará este repositorio.
    - `Long`: Es el tipo de la clave primaria de la entidad `Project` (que es `id` de tipo `Long` en `BaseEntity`).
- **Métodos Personalizados**: Jakarta Data también permite definir métodos de consulta personalizados simplemente siguiendo una convención de nombres (ej. `findByNameLike`, `findByStartDateAfter`). El proveedor de Jakarta Data (EclipseLink) generará la JPQL correspondiente por ti.


## 4. Actualizando el `ProjectService` para usar `ProjectRepository`
Ahora que tenemos nuestro `ProjectRepository`, podemos simplificar drásticamente nuestro `ProjectService`. Ya no necesitamos inyectar `EntityManager` directamente para las operaciones básicas.

Modifica tu clase `ProjectService.java` de la siguiente manera:

```java
package com.tuempresa.proyecto.service;

import com.tuempresa.proyecto.domain.Project;
import com.tuempresa.proyecto.repository.ProjectRepository; // Importa el nuevo repositorio
import jakarta.ejb.Stateless;
import jakarta.inject.Inject; // Necesario para inyectar el repositorio
import java.util.List;
import java.util.Optional; // Para manejar la posibilidad de no encontrar una entidad

@Stateless
public class ProjectService {

    @Inject // Inyectamos nuestra interfaz de repositorio. ¡El contenedor la implementa!
    private ProjectRepository projectRepository;

    /**
     * Crea un nuevo proyecto en la base de datos o actualiza uno existente.
     * save() de CrudRepository maneja ambos casos.
     * @param project El objeto Project a persistir o actualizar.
     * @return El proyecto persistido/actualizado.
     */
    public Project createOrUpdateProject(Project project) {
        return projectRepository.save(project);
    }

    /**
     * Busca un proyecto por su ID.
     * @param id El ID del proyecto.
     * @return Un Optional que contiene el proyecto si se encuentra, o vacío si no.
     */
    public Optional<Project> findProjectById(Long id) {
        return projectRepository.findById(id); // findById devuelve un Optional
    }

    /**
     * Elimina un proyecto de la base de datos por su ID.
     * @param id El ID del proyecto a eliminar.
     */
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    /**
     * Obtiene todos los proyectos de la base de datos.
     * @return Una lista de todos los proyectos.
     */
    public List<Project> findAllProjects() {
        return projectRepository.findAll().stream().toList(); // findAll devuelve un Iterable, lo convertimos a List
    }

    /**
     * Ejemplo de consulta personalizada: encuentra proyectos cuyo nombre contenga un texto.
     * @param nameQuery El texto a buscar en el nombre del proyecto.
     * @return Lista de proyectos coincidentes.
     */
    public List<Project> findProjectsByName(String nameQuery) {
        return projectRepository.findByNameLike("%" + nameQuery + "%"); // Usamos '%' para búsqueda "like"
    }
}
```

**Comparación con el enfoque anterior:**

- Observa cómo los métodos `createProject`, `updateProject`, `findProjectById`, `deleteProject` y `findAllProjects` se han simplificado enormemente, delegando el trabajo al `projectRepository`.

- Ahora también tenemos un ejemplo de cómo usar uno de los métodos de consulta personalizados definidos en la interfaz del repositorio (`findByNameLike`).

## 5. Ajustes en `ProjectResource` (Opcional, pero recomendado)

Para reflejar el uso de Jakarta Data y la nueva forma de `findProjectById` (que devuelve un `Optional`), podemos hacer un pequeño ajuste en `ProjectResource`.

Modifica el método `getProjectById` y `updateProject` en tu `ProjectResource.java`:

```java
package com.tuempresa.proyecto.resources;

import com.tuempresa.proyecto.domain.Project;
import com.tuempresa.proyecto.service.ProjectService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importar Optional

@Path("projects")
public class ProjectResource {

    @Inject
    private ProjectService projectService;

    // ... otros métodos (hello, createProject) sin cambios

    @POST // Usamos el nuevo método genérico save del servicio
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProject(Project project) {
        Project createdProject = projectService.createOrUpdateProject(project); // Ahora usamos createOrUpdateProject
        return Response.status(Response.Status.CREATED).entity(createdProject).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProjectById(@PathParam("id") Long id) {
        Optional<Project> project = projectService.findProjectById(id); // Recibimos un Optional
        if (project.isPresent()) { // Verificamos si hay un valor presente
            return Response.ok(project.get()).build(); // Obtenemos el Project del Optional
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProject(@PathParam("id") Long id, Project updatedProject) {
        Optional<Project> existingProjectOpt = projectService.findProjectById(id); // Recibimos un Optional
        if (existingProjectOpt.isPresent()) {
            Project existingProject = existingProjectOpt.get();
            // Actualizar campos del proyecto existente
            existingProject.setName(updatedProject.getName());
            existingProject.setDescription(updatedProject.getDescription());
            existingProject.setStartDate(updatedProject.getStartDate());
            existingProject.setEndDate(updatedProject.getEndDate());
            
            // Usamos el mismo método createOrUpdateProject para actualizar
            Project result = projectService.createOrUpdateProject(existingProject); 
            return Response.ok(result).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteProject(@PathParam("id") Long id) {
        // No necesitamos verificar si existe, deleteById lo maneja
        projectService.deleteProject(id);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public List<Project> getAllProjects() {
        return projectService.findAllProjects();
    }

    // Nuevo endpoint para probar la consulta personalizada de Jakarta Data
    @GET
    @Path("search/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Project> searchProjectsByName(@PathParam("name") String name) {
        return projectService.findProjectsByName(name);
    }
    
    // El método createTestProject se mantiene igual por simplicidad de prueba rápida
    // ...
}
```

## 6. Prueba de la Aplicación con Jakarta Data

1. Guarda todos los archivos: `pom.xml`, `ProjectRepository.java`, `ProjectService.java` y `ProjectResource.java`.
2. **Redespliega la aplicación en GlassFish desde tu IDE**. Asegúrate de que Maven haya descargado la nueva dependencia de Jakarta Data.
3. Vuelve a usar Postman/Insomnia o **curl** para probar los endpoints. Las operaciones CRUD deberían seguir funcionando exactamente igual, pero ahora están respaldadas por la implementación de Jakarta Data.
    - Prueba el nuevo endpoint:
        - URL: http://localhost:8080/mi-proyecto-pm/api/projects/search/{texto_a_buscar}
        - Método: `GET`
        - Ejemplo: http://localhost:8080/mi-proyecto-pm/api/projects/search/Test (Si creaste proyectos de prueba con "Test Project")

---

¡Excelente! Ahora tu aplicación no solo usa Jakarta Persistence directamente, sino que también aprovecha Jakarta Data para simplificar el acceso a datos. Esta es una práctica muy común y recomendada en aplicaciones Jakarta EE modernas para reducir el código repetitivo y hacer el código más legible.

Continuaremos mañana con el Día 4, donde nos enfocaremos en las Relaciones entre Entidades (uno a muchos, muchos a muchos) en Jakarta Persistence, lo que nos permitirá modelar la complejidad real de nuestro Sistema de Gestión de Proyectos.

¿Todo claro con esta implementación de Jakarta Data?



