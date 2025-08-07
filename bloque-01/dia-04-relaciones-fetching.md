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

### c. Relación `@ManyToMany` (Breve mención)

Aunque no la implementaremos en este modelo, `@ManyToMany` se usa cuando muchas entidades se relacionan con muchas otras (por ejemplo, muchos usuarios en muchos grupos). En Jakarta Persistence, esto generalmente implica una tabla de unión que Jakarta Persistence gestiona automáticamente con esta anotación.

## 2. Estrategias de Fetching y el Problema N+1

El _fetching_ se refiere a cómo Jakarta Persistence carga las entidades relacionadas de la base de datos. Existen dos estrategias principales:

- `FetchType.LAZY` (Perezoso): La colección o entidad relacionada no se carga de la base de datos hasta que la necesites. Este es el valor por defecto para las colecciones (`@OneToMany`, `@ManyToMany`) y es la estrategia recomendada.
- `FetchType.EAGER` (Ansioso): La colección o entidad relacionada se carga inmediatamente junto con la entidad principal. Es el valor por defecto para las relaciones de un solo valor (`@ManyToOne`, `@OneToOne`).

### El Problema N+1

Este es un problema de rendimiento común que ocurre con un uso inadecuado de `FetchType.EAGER`.

Imagina que quieres obtener la lista de todos tus proyectos. Si la relación `@OneToMany` con las tareas fuera `EAGER`, Jakarta Persistence haría las siguientes consultas:

1. Una consulta para obtener todos los proyectos.
2. Una consulta adicional por cada proyecto para cargar su lista de tareas.

