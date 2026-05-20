# Expo 02: El camino de la experiencia

Esta carpeta contiene la versión de `ProjectTracker` usada para el segundo acto de la charla: **cómo entregar una aplicación usable con Jakarta EE 11 sin asumir automáticamente una SPA**.

No reemplaza al tutorial por sesiones. Es una foto preparada para exposición, centrada en UI server-side, seguridad integrada y actualización en tiempo real.

## Qué cubre

- **Jakarta Faces** construye una interfaz server-side con XHTML y backing beans CDI.
- **Jakarta CDI** conecta la vista, los servicios y los eventos de dominio.
- **Jakarta Security** protege tanto la UI como la API.
- **Jakarta Authentication** implementa un mecanismo híbrido para sesión web y JWT.
- **Jakarta REST** conserva la API para clientes externos.
- **Jakarta WebSocket** permite notificaciones en tiempo real hacia el dashboard.
- **CDI Events** desacopla la creación de proyectos del broadcast WebSocket.
- **Jakarta Persistence, Validation y Data** siguen sosteniendo el camino del dato.

La idea central de este acto es mostrar que una aplicación empresarial interna puede tener UI, login, API protegida y actualizaciones en tiempo real sin añadir automáticamente un frontend separado.

## Componentes principales

- `index.xhtml` y `ProjectBean`: pantalla server-side para listar y crear proyectos.
- `login.xhtml` y `LoginBean`: flujo de autenticación web.
- `SecurityConfig`: usuarios, roles y configuración Jakarta Security.
- `HybridAuthenticationMechanism`: seguridad híbrida para UI con sesión y API con JWT.
- `AuthResource` y `TokenService`: login REST y emisión de tokens.
- `ProjectResource`: API REST con `@PermitAll` y `@RolesAllowed`.
- `ProjectService` y `ProjectCreatedEvent`: creación de proyectos y publicación de evento CDI.
- `ProjectDashboardEndpoint` y `DashboardSessionManager`: canal WebSocket para el dashboard.

## Diagrama

```mermaid
flowchart TB
    Browser[Navegador] --> Faces[Jakarta Faces<br/>index.xhtml / login.xhtml]
    Faces --> Beans[Backing beans CDI<br/>ProjectBean / LoginBean]
    Beans --> Security[Jakarta Security<br/>SecurityContext]
    Security --> Hybrid[Jakarta Authentication<br/>HybridAuthenticationMechanism]
    Hybrid --> Store[InMemoryIdentityStore<br/>Usuarios y roles]
    Beans --> Service[ProjectService<br/>Caso de uso]

    ApiClient[Cliente API] --> REST[Jakarta REST<br/>AuthResource / ProjectResource]
    REST --> Hybrid
    REST --> Token[TokenService<br/>JWT]
    REST --> Service

    Service --> Data[Jakarta Data<br/>ProjectRepository]
    Data --> JPA[Jakarta Persistence<br/>Entidades]
    JPA --> DB[(Base de datos)]

    Service --> Event[CDI Event<br/>ProjectCreatedEvent]
    Event --> WsManager[DashboardSessionManager<br/>@Observes]
    WsManager --> WebSocket[Jakarta WebSocket<br/>ProjectDashboardEndpoint]
    WebSocket --> Browser
```

## Demo sugerida

1. Entrar a `index.xhtml` y mostrar redirección a `login.xhtml`.
2. Iniciar sesión con un usuario del `InMemoryIdentityStore`.
3. Mostrar la pantalla de proyectos con Jakarta Faces.
4. Crear un proyecto desde la UI.
5. Mostrar un endpoint REST protegido con JWT y `@RolesAllowed`.
6. Si el entorno está estable, mostrar la actualización por WebSocket al crear un proyecto.

## Mensaje para la charla

Esto no significa que toda aplicación deba usar Jakarta Faces. Significa que no toda aplicación empresarial necesita pagar el costo de una SPA. Para sistemas internos, administrativos y transaccionales, Jakarta EE 11 permite construir una experiencia completa con menos piezas móviles y con Java como columna vertebral.
