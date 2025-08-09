# Día 2: Diseño de Entidades y Jakarta Persistence (Antes JPA) Básico

Hoy nos centraremos en **Jakarta Persistence**, el estándar de Jakarta EE para la persistencia de objetos Java en bases de datos relacionales. Aprenderemos a diseñar nuestras entidades Java que representarán las tablas de nuestra base de datos y a configurarlas para que Jakarta Persistence pueda gestionarlas.

## 1. Conceptos Clave de Jakarta Persistence (Antes JPA)

Antes de empezar, un repaso rápido:

- **Entidad (Entity)**: Una clase Java simple que representa una tabla en la base de datos. Cada instancia de la clase corresponde a una fila en esa tabla.
- **EntityManager**: La interfaz central de Jakarta Persistence para interactuar con la base de datos. Se usa para persistir, actualizar, eliminar y buscar entidades.
- **Unidad de Persistencia (Persistence Unit)**: Define un conjunto de clases de entidad y la configuración de conexión a la base de datos que el EntityManager usará. Se configura en el archivo `persistence.xml`.

## 2. Configuración de la Base de Datos

Para este tutorial, usaremos MariaDB como base de datos. Para iniciar la base de datos de este ejemplo, ejecuta el siguiente `docker-compose.yml` para que tengas toda la configuración necesaria:

- [docker-compose-yml](../source-code/mariadb/docker-compose.yml)

### 2.1 Preparar el Driver JDBC para GlassFish

1. Descargar el Driver JDBC de MariaDB desde esta ubicación: [mariadb-java-client:3.5.4](https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/3.5.4/mariadb-java-client-3.5.4.jar)
2. Desde una ventana de comandos, entrar a la ubicación de GlassFish, en la carpeta donde está el directorio `bin`

   ![](https://i.imgur.com/r5ex9MD.png)
3. Iniciar GlassFish con el siguiente comando:
   ```powershell
   .\asadmin start-domain
   ``` 
   Y esperar hasta que salga el mensaje `Command start-domain executed successfully.`
   ![](https://i.imgur.com/9nR8Ju4.png)
4. Ejecutar el siguiente comando para agregar el driver de MariaDB en GlassFish
   ```powershell
   .\asadmin add-library --type common C:\PATH_FROM_JAR\mariadb-java-client-3.5.4.jar
   ```
   Donde `PATH_FROM_JAR` es la ubicación donde se encuentra el archivo .jar que ha bajado en el paso 1.

   Al ejecutar el comando, deberá aparecer el siguiente resultado
  
   ![](https://i.imgur.com/zv7D2m1.png)
  
5. Reiniciar el GlassFish para que acepte los cambios. Para reiniciar, haga los siguientes comandos:
   ```powershell
   .\asadmin stop-domain
   .\asadmin start-domain
   ```

### 2.2. Configurar un Pool de Conexiones y JDBC Resource

Para una configuración más controlada, podemos crear nuestro propio pool y recurso JDBC.

1. En la consola de GlassFish, ve a **JDBC** > **JDBC Connection Pools**.

2. Haz clic en **New....**
    - **Pool Name**: `ProjectManagerPool`
    -  **Type**: `javax.sql.DataSource`
    - **Database Driver Vendor**: Nada, porque lo configuraremos manualmente en la siguiente pantalla
    - Haz clic en **Next**.
3.  En la siguiente pantalla, confirma las propiedades. Por defecto, Derby ya está configurado. Coloquemos los siguientes valores para crear y conectarnos a la base de datos:
    - `DatabaseName` :  `ProjectManagerDB`.
    - Datasource ClassName: `org.mariadb.jdbc.MariaDbDataSource` Que es la clase DataSource para MariaDB. Como se configuró en el paso anterior, debería encontrarlo sin problema.
    - Activar la opción "Ping", para que valide de que se configuró correctamente la conexión.
    - En la parte inferior, agregar las siguientes propiedades:
      - `Url`: `jdbc:mariadb://localhost/pm`
      - `User` : `pm`
      - `Password`: `pm`
    - Los demás campos se quedan por omisión
    - Haz clic en **Finish**.
4. Después de crear el pool, selecciónalo y haz clic en **Ping** para verificar la conexión.
5.  Ahora, ve a **JDBC** > **JDBC Resources**.
6.  Haz clic en **New....**
    - **JNDI Name**: `jdbc/pmdb` (este será el nombre que usará nuestra aplicación para encontrar el DataSource).
    - **Pool Name**: Selecciona `ProjectManagerPool`.
    - Haz clic en **OK**.


## 3. Las dependencias para este proyecto

Abramos el archivo [`pom.xml`](../source-code/dia-02/project-manager/pom.xml) y agreguemos la siguiente dependencia

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.2.0</version>
    <scope>provided</scope>
</dependency>
```
Esa dependencia es para usar las funcionalidades de Jakarta Persistence.

Además, para este proyecto, usaremos algunas validaciones en las entidades. Para ello, usaremos esta dependencia.

```xml
<dependency>
    <groupId>jakarta.validation</groupId>
    <artifactId>jakarta.validation-api</artifactId>
    <version>3.1.1</version>
    <scope>provided</scope>
</dependency>
```

Se verá con más detalle en el día 6.

## 4. El Archivo `persistence.xml`

Este archivo es crucial para Jakarta Persistence. Define cómo tus entidades se mapean a la base de datos.

1. Crea la carpeta `META-INF` dentro de `src/main/resources` de tu proyecto.
2. Dentro de `src/main/resources/META-INF`, crea un archivo llamado [`persistence.xml`](../source-code/dia-02/project-manager/src/main/resources/META-INF/persistence.xml).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="3.0"
             xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd">

    <persistence-unit name="pm-pu" transaction-type="JTA">
        <jta-data-source>jdbc/pmdb</jta-data-source>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="eclipselink.logging.parameters" value="true"/>
        </properties>
    </persistence-unit>

</persistence>
```

**Explicación de las propiedades clave:**
- `persistence-unit name="pm-pu"`: Nombre de nuestra unidad de persistencia. Lo usaremos para inyectar el `EntityManager`.
- `transaction-type="JTA"`: Indica que usaremos transacciones gestionadas por el contenedor (Jakarta Transactions API), lo cual es lo común en Jakarta EE.
- `<jta-data-source>jdbc/pmdb</jta-data-source>`: Especifica el JNDI Name de la fuente de datos (DataSource) que GlassFish nos proporciona. **Asegúrate de que este JNDI Name coincida con el que configuraste en GlassFish**.
- `jakarta.persistence.schema-generation.database.action`: Controla cómo Jakarta Persistence interactúa con el esquema de la base de datos.
    - `drop-and-create`: Borra todas las tablas existentes y las vuelve a crear cada vez que se despliega la aplicación. **Útil para desarrollo, ¡nunca en producción!**
    - `create`: Crea las tablas si no existen.
    - `none`: No hace nada con el esquema (espera que las tablas ya existan).
- `eclipselink.logging.parameters`: Nivel de log para EclipseLink (la implementación de Jakarta Persistence de GlassFish). `FINE` muestra los parámetros  enlazados al SQL, incluyendo los valores de las excepciones y de los registros obtenidos.
 
## 5. Diseño de las Entidades del Sistema de Gestión de Proyectos

Ahora, vamos a crear las clases Java que representarán las tablas de nuestra base de datos.

### 5.1. Clase Base `BaseEntity.java` (Opcional, pero recomendado)

Para tener campos comunes como `id` y simplificar futuros desarrollos, podemos crear una clase base abstracta.

Crea un nuevo paquete en `src/main/java`, por ejemplo, `com.tuempresa.proyecto.domain`. Dentro de este paquete, crea [`BaseEntity.java`](../source-code/dia-02/project-manager/src/main/java/com/tuempresa/proyecto/domain/BaseEntity.java):

```java
package com.tuempresa.proyecto.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Objects;

@MappedSuperclass // Indica que esta clase no es una entidad, pero sus atributos se mapearán en subclases.
public abstract class BaseEntity implements Serializable {

    @Id // Marca el campo como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Estrategia de generación de ID (IDENTITY para DBs que autoincrementan)
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Métodos equals y hashCode son importantes para la gestión de entidades por Jakarta Persistence
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BaseEntity that = (BaseEntity) obj;
        return Objects.equals(id, that.id);
    }
}
```

### 5.2. Entidad Project.java

Esta entidad representará un proyecto en nuestro sistema.

Crea [`Project.java`](../source-code/dia-02/project-manager/src/main/java/com/tuempresa/proyecto/domain/Project.java) en el mismo paquete `com.tuempresa.proyecto.domain`:

```java
package com.tuempresa.proyecto.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank; // Para validaciones, lo veremos en el Día 6
import jakarta.validation.constraints.Size;
import java.time.LocalDate; // Para fechas, Jakarta EE soporta java.time

@Entity // Marca la clase como una entidad Jakarta Persistence, se mapeará a una tabla llamada 'Project' por defecto
@Table(name = "PROJECTS") // Opcional: Especifica el nombre de la tabla si es diferente al nombre de la clase
public class Project extends BaseEntity {

    @NotBlank(message = "El nombre del proyecto no puede estar vacío.")
    @Size(max = 100, message = "El nombre del proyecto no puede exceder 100 caracteres.")
    private String name;

    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    // Constructor vacío (requerido por Jakarta Persistence)
    public Project() {
    }

    // Constructor con parámetros (opcional, para conveniencia)
    public Project(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate; // Asegura que endDate sea LocalDate
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Project{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", description='" + description + '\'' +
               ", startDate=" + startDate +
               ", endDate=" + endDate +
               '}';
    }
}
```

**Notas importantes:**
- `@Entity`: Anotación fundamental que le dice a Jakarta Persistence que esta clase es una entidad persistente.
- `@Table`: Opcional, pero buena práctica para especificar explícitamente el nombre de la tabla.
- **Campos de Instancia**: Los campos se mapean automáticamente a columnas de la tabla.
- **Constructores**: Jakarta Persistence requiere un constructor público o protegido sin argumentos. Puedes añadir otros constructores.
- **Getters y Setters**: Son necesarios para que Jakarta Persistence acceda a los valores de los campos.
- **@NotBlank** y **@Size**: Estas son anotaciones de Bean Validation, las exploraremos en el Día 6. Por ahora, solo las dejamos ahí.

### 5.3. Entidad `User.java`
Aunque ya tenemos tablas `USERS` y `USER_GROUPS` para el Realm de seguridad, es buena práctica tener una entidad `User` también para la lógica de negocio de la aplicación.

Crea [`User.java`](../source-code/dia-02/project-manager/src/main/java/com/tuempresa/proyecto/domain/User.java) en `com.tuempresa.proyecto.domain`:

```java
package com.tuempresa.proyecto.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "APP_USERS") // Para diferenciarla de la tabla USERS del realm si es necesario
public class User extends BaseEntity {

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    @Size(max = 50, message = "El nombre de usuario no puede exceder 50 caracteres.")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    private String password; // NOTA: En un sistema real, NUNCA almacenar contraseñas en texto plano. Usar hashing.

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "Formato de correo electrónico inválido.")
    private String email;

    @Size(max = 100, message = "El nombre completo no puede exceder 100 caracteres.")
    private String fullName;

    public User() {
    }

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    // Getters y Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", fullName='" + fullName + '\'' +
               '}';
    }
}
```

## 6. `JpaProvider.java` como proveedor de `EntityManager`

Necesitamos una clase que maneje el `EntityManager`, y ésta provea a todas las demás clases la conexión. Para ello, lo instanciamos usando la anotación `@PersistenceContext`, y para que esté disponible para todos, usamos la anotación `@Produces`.

```java
package com.tuempresa.proyecto.provider;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@ApplicationScoped
public class JpaProvider {

    @Produces
    @PersistenceContext(name = "pm-pu")
    private EntityManager em;
}
```
- `@PersistenceContext(unitName = "pm-pu")`: Esta es la joya de la inyección. Le dice al contenedor Jakarta EE que inyecte una instancia de `EntityManager` en la variable `em`.
    - `unitName = "pm-pu"`: Es crucial. Indica a Jakarta Persistence qué unidad de persistencia (definida en `persistence.xml`) debe usar para este `EntityManager`. Asegúrate de que el nombre (`pm-pu`) coincida exactamente con el que definiste en tu `persistence.xml` en el paso 4.


## 7. Verificación y Despliegue

1. **Guarda todos los archivos**: `persistence.xml`,  `BaseEntity.java`, `Project.java`, `User.java`, `JpaProvider.java`.
2. **Re-despliega la aplicación**:
- **En Eclipse**: Haz clic derecho en tu servidor proyecto, seleccion "Run As > Run On Server" .
 
  ![](https://i.imgur.com/6SdZBeM.png)
  
  Selecciona el servidor GlassFish
  ![](https://i.imgur.com/SvmNfJY.png)
  Y hacer clic en "Finish". 
- **En NetBeans**: Haz clic derecho en tu proyecto y selecciona `Clean and Build`, luego `Run`.
  ![](https://i.imgur.com/OqEqC1j.png) 

Cuando la aplicación se despliegue, GlassFish y Jakarta Persistence (EclipseLink) leerán tu `persistence.xml`. Gracias a `drop-and-create`, las tablas `PROJECTS` y `APP_USERS` deberían crearse automáticamente en la base de datos de MariaDB. 

Puedes revisar los logs de GlassFish (en la pestaña `Console` de tu IDE o en el archivo `server.log` dentro de la carpeta `glassfish7/glassfish/domains/domain1/logs`) para ver las sentencias SQL que Jakarta Persistence ha ejecutado. 

En Windows, con PowerShell, puedes usar este comando

```powershell
 Get-Content .\glassfish7\glassfish\domains\domain1\logs\server.log -Tail 100 -Wait
```

Deberías ver algo como `Loading application [project-manager] at [/project-manager]` y `project-manager was successfully deployed...`.

![](https://i.imgur.com/d9URgHP.png)


## Vídeo demostrativo

[![Watch the video](https://img.youtube.com/vi/5NgNlQIQVHk/default.jpg)](https://youtu.be/5NgNlQIQVHk)


## El código fuente

Puedes obtener el código fuente de esta sesión en la siguiente ubicación:

[project-manager | Día 02](../source-code/dia-02)


---

¡Felicidades! Has configurado Jakarta Persistence, definido tu unidad de persistencia y diseñado tus primeras entidades. Esto sienta las bases para almacenar y gestionar datos en tu Sistema de Gestión de Proyectos.

Mañana, en el Día 3, aprenderemos a realizar operaciones **CRUD básicas** (**Crear**, **Leer**, **Actualizar**, **Borrar**) con estas entidades utilizando el `EntityManager`.

¿Pudiste ver las tablas creadas en los logs de GlassFish? Si tienes alguna duda o problema, házmelo saber.
