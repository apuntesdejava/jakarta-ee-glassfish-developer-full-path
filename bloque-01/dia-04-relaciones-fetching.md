# Día 4: Relaciones de Entidades y Fetching

Hoy vamos a ir más allá de las entidades independientes y modelar las conexiones reales de nuestra aplicación de gestión de proyectos. Usaremos las anotaciones de relaciones de JPA para vincular nuestras clases y entenderemos cómo optimizar el rendimiento al cargar datos relacionados.

## 1. Mapeo de Relaciones: `@OneToMany`, `@ManyToOne` y `@ManyToMany`

Las anotaciones de relaciones en Jakarta Persistence nos permiten definir cómo una entidad se relaciona con otra.

### a. La Entidad `Task` (Lado `ManyToOne`)

Para demostrar las relaciones, necesitamos una entidad más: `Task`. Una tarea pertenece a un solo proyecto y puede ser asignada a un solo usuario. Esta es una relación de Muchos a Uno (`@ManyToOne`).

Crea una nueva clase `Task.java` en el paquete `com.tuempresa.proyecto.domain`:

```java
package com.tuempresa.proyecto.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "TASKS")
public class Task extends BaseEntity {

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    // Esta tarea pertenece a UN solo proyecto.
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Esta tarea es asignada a UN solo usuario.
    @ManyToOne
    @JoinColumn(name = "assigned_user_id")
    private User assignedUser;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }
 
}

```

- `@ManyToOne`: Anotación fundamental para el lado "muchos" de la relación.
- `@JoinColumn`: Especifica la columna de clave foránea que se creará en la tabla `TASKS` para almacenar el ID del proyecto o del usuario.

### b. Actualizando `Project` y `User` (Lado `OneToMany`)

Ahora, actualicemos las clases `Project` y `User` para reflejar la relación bidireccional. Esto nos permitirá acceder a la lista de tareas desde un proyecto o un usuario.

**Actualiza** `Project.java`:

```java
// ... dentro de la clase Project

import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "PROJECTS")
public class Project extends BaseEntity {

    // ... campos existentes

    // UN proyecto tiene MUCHAS tareas.
    // 'mappedBy' indica que la clave foránea está en la entidad Task
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

    // ... getters y setters...
    public Set<Task> getTasks() { return tasks; }
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
}
```

El código fuente completo de esta clase lo puedes encontrar aquí: [Project.java](../source-code/dia-04/project-manager/src/main/java/com/tuempresa/proyecto/domain/Project.java)

**Actualiza** `User.java`:

```java
// ... dentro de la clase User

import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "APP_USERS")
public class User extends BaseEntity {

    // ... campos existentes

    // UN usuario tiene MUCHAS tareas asignadas.
    @OneToMany(mappedBy = "assignedUser", fetch = FetchType.LAZY)
    private Set<Task> assignedTasks = new HashSet<>();

    // ... getters y setters...
    public Set<Task> getAssignedTasks() { return assignedTasks; }
    public void setAssignedTasks(Set<Task> assignedTasks) { this.assignedTasks = assignedTasks; }
}
```

El código fuente completo de esta clase lo puedes encontrar aquí: [User.java](../source-code/dia-04/project-manager/src/main/java/com/tuempresa/proyecto/domain/User.java)

- `@OneToMany`: Anotación para el lado "uno" de la relación.
- `mappedBy`: Crucial para las relaciones bidireccionales. Evita que Jakarta Persistence cree una tabla o columna extra. Simplemente le dice a Jakarta Persistence que la columna de la clave foránea ya existe en el otro lado de la relación (en la entidad `Task`).

> **Prevención de error cíclico en objetos**
> 
> Como estás notando, un objeto `Project` tendrá objetos `Task`, y un objeto `Task` tendrá un objeto `Project`. A nivel de objetos no hay problema, porque es la representación natural de los datos. Pero al querer invocar a un endpoint REST de proyectos, esto causará un problema de datos al generar el JSON 
> ```
> jakarta.json.bind.JsonbException: Unable to serialize property 'tasks' from com.tuempresa.proyecto.domain.Project
> at org.eclipse.yasson.internal.serializer.ObjectSerializer.lambda$serialize$0(ObjectSerializer.java:43)
> at java.base/java.util.LinkedHashMap.forEach(LinkedHashMap.java:986)
> ```
> Solo para este ejemplo, vamos a solucionarlo ignorando la creación del JSON del campo `project` de la clase `Task`. Para ello agregaremos la anotación `@JsonbTransient`
> 
> ```java
>    @ManyToOne
>    @JoinColumn(name = "project_id", nullable = false)
>    @JsonbTransient //ignoramos este campo en el JSON
>    private Project project;
> ```
> 
> La mejor manera es usando el DTO, pero lo veremos en otro apartado.

### c. Relación `@ManyToMany` (Breve mención)

Aunque no la implementaremos en este modelo, `@ManyToMany` se usa cuando muchas entidades se relacionan con muchas otras (por ejemplo, muchos usuarios en muchos grupos). En Jakarta Persistence, esto generalmente implica una tabla de unión que Jakarta Persistence gestiona automáticamente con esta anotación.

## 2. Estrategias de Fetching y el Problema N+1

El _fetching_ se refiere a cómo Jakarta Persistence carga las entidades relacionadas de la base de datos. Existen dos estrategias principales:

- `FetchType.LAZY` (Perezoso): La colección o entidad relacionada no se carga de la base de datos hasta que la necesites. Este es el valor por defecto para las colecciones (`@OneToMany`, `@ManyToMany`) y es la estrategia recomendada.
- `FetchType.EAGER` (Ansioso): La colección o entidad relacionada se carga inmediatamente junto con la entidad principal. Es el valor por defecto para las relaciones de un solo valor (`@ManyToOne`, `@OneToOne`).

### El Problema N+1

Este es un problema de rendimiento común que ocurre con un uso inadecuado de `FetchType.EAGER`.

#### Configurando el entorno

Para demostrarlo, vamos a hacer algunas configuraciones en nuestro servidor GlassFish y en nuestra aplicación. Justamente lo que necesitamos es ver en el log los queries que se ejecuta cada vez que hacemos una petición. Para ello, entremos al administrador Glassfish http://localhost:4848

1. En el panel izquierdo entremos a la sección Configurations > server-config > Logger Settings

   ![](https://i.imgur.com/bHFvBDL.png) 
  
2. Hacer clic en la ficha "Log Levels"
  
   ![](https://i.imgur.com/6EYBiAs.png) 
  
   Te aparecerá una lista de los paquetes que están siendo monitoreados por el Log. Así que tendremos que agregar el que necesitamos, y el nivel de log para hacerle seguimiento.
3. Hacemos clic en el botón "Add Logger", y aparecerá una casilla en blanco
  
   ![](https://i.imgur.com/hq5TH5R.png)  

   Allí escribimos el siguiente paquete:
   - Logger Name: `org.eclipse.persistence.session`
   - Log Level: `FINE`
   
   ![](https://i.imgur.com/eVe4kDY.png)
  
4. Finalmente, hacer clic en "Save" para guardar los cambios. 

#### Cargando datos

Adicionalmente, vamos a preparar nuestra aplicación para que tenga datos precargados y así veamos mejor el problema del N+1.

Para ello, vamos a crear un archivo llamado `data.sql` y allí pondremos todos los inserts que irán en las tablas. Ese archivo lo pondremos en la siguiente ubicación: `src/main/resources/META-INF/sql`

Un ejemplo de este contenido, lo podemos encontrar aquí: [src/main/resources/META-INF/sql/data.sql](../source-code/dia-04/project-manager/src/main/resources/META-INF/sql/data.sql)

> Naturalmente, en un entorno real, no se debe cargar tanta información como en este ejemplo.  Solo se está usando este archivo para fines de aprendizaje.

Y, para que se ejecute ese script para poblar la base de datos cuando se inicie el proyecto, debemos agregar esta propiedad en el archivo `persistence.xml`

```xml
<property name="jakarta.persistence.sql-load-script-source" value="META-INF/sql/data.sql"/>
```

El archivo completo de `persistence.xml` lo puedes encontrar aquí: [persistence.xml](../source-code/dia-04/project-manager/src/main/resources/META-INF/persistence.xml)


#### Revisando el log

El log no se podrá visualizar desde el IDE, por lo que tenemos que revisar directamente el archivo `$GLASSFISH_HOME/glassfish/domains/domain1/logs/server.log`

Desde Windows, podemos usar este comando en PowerShell:

```powershell
get-content .\server.log -Tail 10 -wait
```

Y desde bash, se hace con:

```shell
tail -f ./server.log
```

Todo cambio que suceda en el archivo `server.log` lo veremos en tiempo real.

#### Ejemplo N+1

Vamos a ejecutar el proyecto y llamar al endpoint de ver un solo proyecto, y de listar todos los proyectos, y veremos lo que genera aparece en el `server.log`.

Una que esté arriba el proyecto:

![](https://i.imgur.com/BIxM2jD.png)

Vamos a llamar al endpoint para obtener la información del proyecto con id 1:

```powershell
$response = Invoke-RestMethod -Uri 'http://localhost:8080/project-manager/rest/projects/1' -Method GET 
$response | ConvertTo-Json
```

Y veremos que el log genera lo siguiente:

![](https://i.imgur.com/YVWTCu6.png)

Una petición genera dos consultas. Y es porque solo tiene esa relación. Si tuviera más relaciones tendría más queries.

¿Y si ejecutamos el listado completo de proyectos?

```powershell
$response = Invoke-RestMethod -Uri 'http://localhost:8080/project-manager/rest/projects' -Method GET 
$response | ConvertTo-Json
```

El log:

![](https://i.imgur.com/ndrTLLd.png)

No hay que analizar tanto para darse cuenta que, por cada proyecto, hará una consulta a la tabla de tareas. Lo cual es muy ineficiente.

#### Solucionando el problema de N+1

Si usáramos un query propio de la misma base de datos, es decir, un query nativo, se podría solucionar. Pero, usaremos lo que nos trae Jakarta Persistence:

##### Usando JPQL

> JPQL significa "Jakarta Persistence Query Language"

Vamos a modificar la consulta del listado de todos los proyectos de la siguiente manera. Clase `ProjectService.java`

```java
public List<Project> findAllProjects() {
    return em.createQuery("SELECT p FROM Project p  LEFT JOIN Fetch p.tasks t", Project.class).getResultList(); //LEFT JOIN FETCH
}
```

Y al invocar al endpoint, este es el log resultante:

![](https://i.imgur.com/uDzfj9H.png)

Después de haberse desplegado del proyecto, solo tiene un solo query en base de datos que es el siguiente:

```sql
  SELECT t1.ID,
         t1.DESCRIPTION,
         t1.ENDDATE,
         t1.NAME,
         t1.STARTDATE,
         t0.ID,
         t0.DESCRIPTION,
         t0.DUEDATE,
         t0.STATUS,
         t0.TITLE,
         t0.assigned_user_id,
         t0.project_id
  FROM PROJECTS t1
           LEFT OUTER JOIN TASKS t0 ON (t0.project_id = t1.ID)
```

Podemos ver que, con un comando simple en JPQL, Jakarta Persistence nos genera un query listo para ser ejecutado.

##### Usando Persistence API

Esta técnica es un poco con más código, pero es más seguro, si uno quiere armar el query de manera programáticamente.

```java
public List<Project> findAllProjects() {
    
    var cb = em.getCriteriaBuilder();
    var cq = cb.createQuery(Project.class);
    var project = cq.from(Project.class);  
    project.fetch("tasks",JoinType.LEFT);
    return em.createQuery(cq).getResultList();
}

```
Y el resultado es el mismo.

## El código fuente

Puedes obtener el código fuente de esta sesión en la siguiente ubicación:

[project-manager | Día 04](../source-code/dia-04)

----

¡Excelente! Con esto, has modelado las relaciones de tu aplicación, lo que es la base de cualquier sistema empresarial.

Mañana, en el Día 5, nos centraremos en la Capa de Servicio con EJB, donde crearemos la lógica para trabajar con estas relaciones de forma transaccional y segura.

¿El contenido de este día es el que tenías en mente?