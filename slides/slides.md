---
theme: nord
title: Jakarta EE 11 en 45 minutos
info: |
  Una charla sobre construir una aplicacion empresarial completa con Java,
  Jakarta EE 11 y Payara, organizada en tres actos: dato, experiencia y produccion.
colorSchema: dark
highlighter: shiki
lineNumbers: true
drawings:
  persist: false
transition: slide-left
mdc: true
---

# Jakarta EE 11 en 45 minutos

## De cero a una aplicacion empresarial completa con Java y Payara

JConf - ProjectTracker

---

# La tesis

Jakarta EE 11 permite construir una aplicacion empresarial completa usando Java como plataforma principal.

No se trata de prohibir Angular, React u otras herramientas.

Se trata de no traerlas por reflejo cuando el estandar ya resuelve el caso.

---

# El ejemplo

ProjectTracker:

- Gestion de proyectos
- Tareas por proyecto
- Usuarios y seguridad
- API REST
- UI server-side
- Eventos en tiempo real
- Procesos asincronos
- Observabilidad
- Despliegue con Payara

[Repositorio del tutorial](../README.md)

---

# No voy a recorrer 16 carpetas

El repositorio esta organizado como sesiones incrementales.

Para una charla de 45 minutos, la historia se entiende mejor como un solo camino:

1. El camino del dato
2. El camino de la experiencia
3. El camino de produccion

[Tabla de contenidos completa](../SUMMARY.md)

---
layout: section
---

# Acto 1

## El camino del dato

Como modelo, valido, expongo y persisto informacion.

---

# Del endpoint al contrato

Primero aparece la superficie HTTP:

- Jakarta REST
- JSON-B
- DTOs con records
- Un contrato simple para crear y consultar proyectos

Sesiones:

- [Sesion 0: Setup y Hola Mundo](../session-00-setup/README.md)
- [Sesion 1: API REST](../session-01-jaxrs/README.md)

---

# CDI como pegamento

La logica deja de vivir en el recurso REST.

Jakarta CDI conecta las piezas:

- `@ApplicationScoped`
- `@Inject`
- Qualifiers
- Separacion entre API y servicio

[Sesion 2: CDI](../session-02-cdi/README.md)

---

# Persistencia real

La aplicacion ya no trabaja con datos en memoria.

Jakarta Persistence permite modelar:

- Entidades
- Relaciones
- Transacciones
- `@Embeddable`
- Records y tipos modernos de Java

[Sesion 3: JPA](../session-03-jpa/README.md)

---

# Validar antes de confiar

La validacion forma parte del contrato.

```java
public record ProjectDTO(
    Long id,
    @NotBlank @Size(min = 3, max = 80) String name,
    String description,
    String status
) {}
```

[Sesion 4: Validation](../session-04-validation/README.md)

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

Menos infraestructura manual, mas intencion de negocio.

[Sesion 5: Jakarta Data](../session-05-data/README.md)

---

# Demo 1

## De API a datos reales

Mostrar:

- `GET /resources/projects`
- `POST /resources/projects`
- Validacion fallando con error claro
- `ProjectRepository`
- Servicio usando repositorio declarativo

Mensaje:

> REST, JSON, validacion, CDI, transacciones y datos bajo contratos estandar.

---
layout: section
---

# Acto 2

## El camino de la experiencia

Puedo entregar una aplicacion usable sin traer una SPA por defecto.

---

# UI server-side

Jakarta Faces permite construir pantallas transaccionales con el mismo modelo de aplicacion:

- XHTML
- Backing beans CDI
- Formularios
- Tablas
- Validacion integrada

[Sesion 6: Jakarta Faces](../session-06-faces/README.md)

---

# Seguridad integrada

La aplicacion necesita proteger UI y API.

Jakarta Security permite combinar:

- Login por formulario
- Roles
- `@RolesAllowed`
- JWT para REST
- Identity store para pruebas y demo

[Sesion 7: Security](../session-07-security/README.md)

---

# Tiempo real sin cambiar de stack

Cuando el modelo cambia, la UI puede recibir eventos.

Jakarta WebSocket + CDI Events:

- `@ServerEndpoint`
- `@Observes`
- Broadcast a sesiones conectadas
- Dashboard reactivo desde Java

[Sesion 11: WebSockets](../session-11-websockets/README.md)

---

# Demo 2

## Aplicacion completa, no solo backend

Mostrar:

- Login
- Pantalla de proyectos
- Crear proyecto desde UI
- Endpoint protegido
- Actualizacion via WebSocket si el entorno esta listo

Mensaje:

> Para muchas apps empresariales, server-side UI reduce piezas moviles sin renunciar a productividad.

---
layout: section
---

# Acto 3

## El camino de produccion

Esto debe poder operar mas alla del laptop.

---

# Concurrencia moderna

Jakarta Concurrency 3.1 se encuentra con Java 21.

```java
@ManagedExecutorDefinition(
    name = "java:app/concurrent/VirtualExecutor",
    virtual = true
)
```

El runtime gestiona contexto, seguridad y ejecucion asincrona.

[Sesion 8: Virtual Threads](../session-08-virtual_threads/README.md)

---

# Trabajo desacoplado

No todo debe ocurrir dentro de la request HTTP.

Jakarta EE cubre varios patrones:

- JMS para mensajeria
- MDB para consumidores
- `@Schedule` para tareas periodicas
- Batch para procesos largos y chunked

Sesiones:

- [Sesion 9: Messaging](../session-09-messaging/README.md)
- [Sesion 10: Schedule](../session-10-schedule/README.md)
- [Sesion 13: Batch](../session-13-batch/README.md)

---

# Observabilidad

Payara integra MicroProfile para operar la aplicacion:

- Health
- Readiness
- Metrics
- Endpoints compatibles con plataformas cloud

[Sesion 12: Health & Metrics](../session-12-health/README.md)

---

# Despliegue

Dos formas de llevarlo a produccion:

- Payara Server en contenedor
- Payara Micro como runtime ligero

Sesiones:

- [Sesion 14: Docker + Payara Server](../session-14-container/README.md)
- [Sesion 15: Payara Micro](../session-15-payaramicro/README.md)

---

# Demo 3

## Moderna y operable

Mostrar:

- Endpoint que dispara un reporte asincrono
- Logs mostrando `VirtualThread`
- `/health`
- `/metrics`
- Empaquetado con Payara Micro

Mensaje:

> La plataforma no termina en escribir endpoints: tambien cubre concurrencia, operacion y despliegue.

---

# El mapa completo

| Acto | Pregunta | Sesiones |
|---|---|---|
| Dato | Como modelo y persisto informacion | 0, 1, 2, 3, 4, 5 |
| Experiencia | Como entrego una app usable | 6, 7, 11 |
| Produccion | Como escalo, opero y despliego | 8, 9, 10, 12, 13, 14, 15 |

[Tabla de contenidos](../SUMMARY.md)

---

# Cuando si usar React o Angular

Usalos cuando aporten valor real:

- Interaccion altamente dinamica
- Estado complejo en cliente
- Equipos frontend especializados
- Ecosistemas de componentes ya adoptados
- Producto publico con experiencia muy rica

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
- Menor tolerancia a integracion accidental

[Sesion 6: Jakarta Faces](../session-06-faces/README.md)

---

# Cierre

Jakarta EE 11 no es una coleccion de APIs viejas.

Es una plataforma estandar para construir aplicaciones completas:

- API
- UI
- Datos
- Seguridad
- Concurrencia
- Mensajeria
- Batch
- Observabilidad
- Despliegue

Todo con Java como columna vertebral.

---

# Preguntas

## Y despues, codigo

[Repositorio](../README.md)
