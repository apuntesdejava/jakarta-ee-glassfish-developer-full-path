---
theme: nord
title: Jakarta EE 11 en 45 minutos
info: |
  Una charla sobre construir una aplicación empresarial completa con Java,
  Jakarta EE 11 y GlassFish 8, organizada en tres actos: dato, experiencia y producción.
colorSchema: dark
highlighter: shiki
lineNumbers: true
drawings:
  persist: false
transition: slide-left
mdc: true
---

# Jakarta EE 11 en 45 minutos

## De cero a una aplicación empresarial completa con Java y GlassFish 8

JConf - ProjectTracker

---

# La tesis

Jakarta EE 11 permite construir una aplicación empresarial completa usando Java como plataforma principal.

No se trata de prohibir Angular, React u otras herramientas.

Se trata de no traerlas por reflejo cuando el estandar ya resuelve el caso.

---

# El ejemplo

ProjectTracker:

- Gestión de proyectos
- Tareas por proyecto
- Usuarios y seguridad
- API REST
- UI server-side
- Eventos en tiempo real
- Procesos asíncronos
- Observabilidad
- Despliegue con GlassFish 8

[Repositorio del tutorial](../README.md)

---

# No voy a recorrer 16 carpetas

El repositorio está organizado como sesiones incrementales.

Para una charla de 45 minutos, la historia se entiende mejor como un solo camino:

1. El camino del dato
2. El camino de la experiencia
3. El camino de producción

[Tabla de contenidos completa](../SUMMARY.md)

---
layout: section
---

# Acto 1

## El camino del dato

Cómo modelo, valido, expongo y persisto información.

---

# Del endpoint al contrato

Primero aparece la superficie HTTP:

- Jakarta REST
- JSON-B
- DTOs con records
- Un contrato simple para crear y consultar proyectos

Sesiones:

- [Sesión 0: Setup y Hola Mundo](../session-00-setup/README.md)
- [Sesión 1: API REST](../session-01-jaxrs/README.md)

---

# CDI como pegamento

La lógica deja de vivir en el recurso REST.

Jakarta CDI conecta las piezas:

- `@ApplicationScoped`
- `@Inject`
- Qualifiers
- Separación entre API y servicio

[Sesión 2: CDI](../session-02-cdi/README.md)

---

# Persistencia real

La aplicación ya no trabaja con datos en memoria.

Jakarta Persistence permite modelar:

- Entidades
- Relaciones
- Transacciones
- `@Embeddable`
- Records y tipos modernos de Java

[Sesión 3: JPA](../session-03-jpa/README.md)

---

# Validar antes de confiar

La validación forma parte del contrato.

```java
public record ProjectDTO(
    Long id,
    @NotBlank @Size(min = 3, max = 80) String name,
    String description,
    String status
) {}
```

[Sesión 4: Validation](../session-04-validation/README.md)

---

# Jakarta Data

El momento clave del Acto 1.

```java
@Repository
public interface ProjectRepository
    extends BasicRepository<Project, Long> {

    List<Project> findByStatus(String status);
}
```

Menos infraestructura manual, más intención de negocio.

[Sesión 5: Jakarta Data](../session-05-data/README.md)

---

# Demo 1

## De API a datos reales

Mostrar:

- `GET /resources/projects`
- `POST /resources/projects`
- Validación fallando con error claro
- `ProjectRepository`
- Servicio usando repositorio declarativo

Mensaje:

> REST, JSON, validación, CDI, transacciones y datos bajo contratos estándar.

---
layout: section
---

# Acto 2

## El camino de la experiencia

Puedo entregar una aplicación usable sin traer una SPA por defecto.

---

# UI server-side

Jakarta Faces permite construir pantallas transaccionales con el mismo modelo de aplicación:

- XHTML
- Backing beans CDI
- Formularios
- Tablas
- validación integrada

[Sesión 6: Jakarta Faces](../session-06-faces/README.md)

---

# Seguridad integrada

La aplicación necesita proteger UI y API.

Jakarta Security permite combinar:

- Login por formulario
- Roles
- `@RolesAllowed`
- JWT para REST
- Identity store para pruebas y demo

[Sesión 7: Security](../session-07-security/README.md)

---

# Tiempo real sin cambiar de stack

Cuando el modelo cambia, la UI puede recibir eventos.

Jakarta WebSocket + CDI Events:

- `@ServerEndpoint`
- `@Observes`
- Broadcast a sesiones conectadas
- Dashboard reactivo desde Java

[Sesión 11: WebSockets](../session-11-websockets/README.md)

---

# Demo 2

## Aplicación completa, no solo backend

Mostrar:

- Login
- Pantalla de proyectos
- Crear proyecto desde UI
- Endpoint protegido
- Actualización via WebSocket si el entorno está listo

Mensaje:

> Para muchas apps empresariales, server-side UI reduce piezas móviles sin renunciar a productividad.

---
layout: section
---

# Acto 3

## El camino de producción

Esto debe poder operar más allá del laptop.

---

# Concurrencia moderna

Jakarta Concurrency 3.1 se encuentra con Java 21.

```java
@ManagedExecutorDefinition(
    name = "java:app/concurrent/VirtualExecutor",
    virtual = true
)
```

El runtime gestiona contexto, seguridad y ejecución asíncrona.

[Sesión 8: Virtual Threads](../session-08-virtual_threads/README.md)

---

# Trabajo desacoplado

No todo debe ocurrir dentro de la request HTTP.

Jakarta EE cubre varios patrones:

- JMS para mensajería
- MDB para consumidores
- `@Schedule` para tareas periódicas
- Batch para procesos largos y chunked

Sesiones:

- [Sesión 9: Messaging](../session-09-messaging/README.md)
- [Sesión 10: Schedule](../session-10-schedule/README.md)
- [Sesión 13: Batch](../session-13-batch/README.md)

---

# Observabilidad

GlassFish 8 integra MicroProfile para operar la aplicación:

- Health
- Readiness
- Métricas Prometheus de la app
- Logs con Loki
- Prometheus + Loki + Grafana como visor local

[Sesión 12: Health & Metrics](../session-12-health/README.md)

---

# Despliegue

Una forma reproducible de llevarlo a producción:

- GlassFish 8 en contenedor
- MySQL como dependencia externa
- Stack O11Y local en Docker Compose

Referencia:

- [Expo 03: Docker + GlassFish 8 + MySQL](../expo-03-prod-path/project-tracker/README.md)

---

# Demo 3

## Moderna y operable

Mostrar:

- Endpoint que dispara un reporte asíncrono
- Logs mostrando `VirtualThread`
- `/health`
- `/metrics`
- Empaquetado con GlassFish 8 + MySQL
- Dashboard local de O11Y
- Logs centralizados con Loki

Mensaje:

> La plataforma no termina en escribir endpoints: también cubre concurrencia, operación y despliegue.

---

# El mapa completo

| Acto        | Pregunta                           | Sesiones                 |
|-------------|------------------------------------|--------------------------|
| Dato        | Como modelo y persisto informacion | 0, 1, 2, 3, 4, 5         |
| Experiencia | Como entrego una app usable        | 6, 7, 11                 |
| Produccion  | Como escalo, opero y despliego     | 8, 9, 10, 12, 13, 14, 15 |

[Tabla de contenidos](../SUMMARY.md)

---

# Cuando si usar React o Angular

Úsalos cuando aporten valor real:

- Interacción altamente dinámica
- Estado complejo en cliente
- Equipos frontend especializados
- Ecosistemas de componentes ya adoptados
- Producto público con experiencia muy rica

La propuesta no es menos frontend.

La propuesta es elegir con criterio.

---

# Cuando Jakarta Faces tiene sentido

Brilla en aplicaciones:

- Internas
- Administrativas
- Transaccionales
- CRUD con seguridad fuerte
- Formularios y tablas
- Equipos Java full-stack
- Menor tolerancia a integración accidental

[Sesión 6: Jakarta Faces](../session-06-faces/README.md)

---

# Cierre

Jakarta EE 11 no es una colección de APIs viejas.

Es una plataforma estándar para construir aplicaciones completas:

- API
- UI
- Datos
- Seguridad
- Concurrencia
- Mensajería
- Batch
- Observabilidad
- Despliegue

Todo con Java como columna vertebral.

---

# Preguntas

## Y después, código

[Repositorio](../README.md)
