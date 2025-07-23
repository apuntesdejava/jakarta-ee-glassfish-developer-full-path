# Día 1: Configuración del Entorno y Primer Proyecto

Hoy vamos a sentar las bases para todo el camino que tenemos por delante. Lo primero es asegurarnos de que tienes todo lo necesario para empezar a desarrollar con [Jakarta EE](https://jakarta.ee/) y [GlassFish](https://glassfish.org/).

## 1. Requisitos Previos

Antes de instalar cualquier cosa, asegúrate de tener estos elementos listos:

* Conexión a Internet Estable: Necesitarás descargar varios archivos grandes.
* Espacio en Disco Suficiente: Los IDEs, servidores de aplicaciones y SDKs pueden ocupar varios GB.

## 2. Java Development Kit (JDK)

Jakarta EE 10 requiere al menos de JDK 11 o superior. Te recomiendo usar la última versión LTS disponible, que actualmente es la JDK 21.

* Descarga. Puedes descargar el JDK desde los siguientes sitios oficiales:
  * [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) (requiere una cuenta de Oracle)
  * [OpenJDK](https://www.google.com/search?q=https://openjdk.java.net/install/index.html) (varias distribuciones como Adoptium Temurin, Amazon Corretto, Azul Zulu). Te recomiendo Adoptium Temurin por ser de código abierto y fácil de usar: [https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/)
  * Instalación: Sigue las instrucciones para tu sistema operativo (Windows, macOS, Linux).
  * Verificación: Abre una terminal o Símbolo del sistema y ejecuta:
    ```shell
    java -version    
    javac -version
    ```
  * Ambos comandos deberían mostrar la versión del JDK que acabas de instalar.

## 3. Entorno de Desarrollo Integrado (IDE)

Para desarrollar aplicaciones Jakarta EE, un buen IDE es fundamental. Mis recomendaciones son:

* IntelliJ IDEA Ultimate Edition: Es el más potente y con mejor soporte para Jakarta EE, pero es de pago. Ofrecen una prueba gratuita pero no incluye el soporte para Jakarta EE.
* Eclipse IDE for Enterprise Java and Web Developers: Una opción gratuita y muy popular.
* NetBeans IDE: También gratuito y con excelente integración con GlassFish.
* Visual Studio Code: Un editor de código muy conocido, pero la configuración para desarrollar en Jakarta EE es un poco complicado.

Recomendación para este tutorial: Si no tienes una preferencia fuerte o si es tu primera vez, te sugiero Eclipse IDE for Enterprise Java and Web Developers o NetBeans IDE. Ambos son excelentes y gratuitos.

* Descarga e Instalación:
  * Eclipse: Ve a [https://www.eclipse.org/downloads/packages/](https://www.eclipse.org/downloads/packages/) y descarga la versión "Eclipse IDE for Enterprise Java and Web Developers".\
    Descomprime el archivo y ejecuta el ejecutable de Eclipse.

  * Apache NetBeans: Ve a [https://netbeans.apache.org/download/](https://netbeans.apache.org/download/) y descarga la última versión. Descomprime el archivo y ejecuta el ejecutable que está en `bin\netbeans`

## 4. Servidor de Aplicaciones GlassFish

GlassFish es nuestro servidor de aplicaciones. Usaremos la versión más reciente que soporta Jakarta EE 10.

Descarga: Ve al sitio de GlassFish: [https://glassfish.org/download](https://glassfish.org/download)

* Busca la versión estable más reciente (ej. GlassFish 7.0.x para Jakarta EE 10).
* Descarga el archivo ZIP o TAR.GZ.

Instalación:

* Descomprime el archivo descargado en una ubicación de tu disco donde tengas permisos de escritura y no haya espacios en el nombre de la carpeta (ej. `C:\glassfish7` en Windows, o `~/glassfish7` en Linux/macOS).
* ¡Importante! Anota esta ruta, la usaremos más adelante.

## 5. Apache Maven (Sistema de Gestión de Proyectos)

Maven es la herramienta estándar para construir y gestionar proyectos Java.

Descarga: Ve a [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

* Descarga el archivo binario (apache-maven-X.Y.Z-bin.zip o .tar.gz).

Instalación:

* Descomprime el archivo en una ubicación de tu disco (ej. `C:\apache-maven-3.9.11` o `~/apache-maven-3.9.11`).
* Configuración de Variables de Entorno:
  * Añade la ruta del directorio bin de Maven a la variable de entorno PATH de tu sistema.
  * Crea una variable de entorno `MAVEN_HOME` apuntando al directorio raíz de Maven (donde lo descomprimiste).
* Verificación: Abre una nueva terminal y ejecuta:
 ```shell
mvn -v
 ```

Debería mostrar la versión de Maven.

## 6. Integración del Servidor GlassFish en el IDE

Ahora que tienes todos los componentes, vamos a integrar GlassFish en tu IDE para facilitar el desarrollo y despliegue.

**Para Eclipse:**
1. Ir a la opción Help > Marketplace y buscar "GlassFish". De la lista que aparece, seleccionar "OmniFish" y hacer clic en "Install". Esperar que termine la instalación. Reiniciar el IDE si es necesario.
2. Ve a Window > Show View > Servers (si no lo ves, ve a Window > Show View > Other... > Server > Servers).
3. En la vista Servers, haz clic derecho y selecciona New > Server.
4. Expande Eclipse Foundation (o GlassFish) y selecciona GlassFish Tools. Luego haz clic en Next.
5. En la siguiente pantalla, navega hasta la carpeta donde descomprimiste GlassFish (ej. `C:\glassfish7`). Haz clic en Finish.
6. Una vez añadido, puedes iniciar GlassFish desde la vista Servers haciendo clic derecho sobre él y seleccionando Start.

**Para NetBeans:**
1. Ve a Services (pestaña en la ventana izquierda, si no la ves, Window > Services). 
2. Haz clic derecho en Servers y selecciona Add Server....
3. Elige GlassFish Server y haz clic en Next.
4. Navega hasta la carpeta donde descomprimiste GlassFish (ej. C:\glassfish7).
5. Sigue los pasos, define el nombre de usuario y contraseña si es necesario (el predeterminado es admin sin contraseña para GlassFish 7), y haz clic en Finish.
6. Para iniciar GlassFish, haz clic derecho sobre el servidor en la pestaña Services y selecciona Start.

## 7. Creación de Tu Primer Proyecto "Hello World" con Jakarta EE

Ahora que todo está configurado, crearemos un proyecto web simple.

**Usando Maven Archetype (Recomendado para empezar, independientemente del IDE):**

Abre tu terminal/Símbolo del sistema y ejecuta el siguiente comando Maven. Asegúrate de reemplazar `com.tuempresa.proyecto` con el nombre de tu paquete base (ej. `com.miempresa.app`) y `project-manager` con el nombre que quieras para tu proyecto:

**En macOS,Linux:**
```shell
mvn archetype:generate \
  -DarchetypeGroupId="org.eclipse.starter" \
  -DarchetypeArtifactId="jakarta-starter" \
  -DarchetypeVersion="2.6.0" \
  -DjakartaVersion="11" \
  -DgroupId="com.tuempresa.proyecto" \
  -DartifactId="project-manager" \
  -Dpackage="com.tuempresa.proyecto" \
  -DinteractiveMode=false
  
```

**En Windows (con PowerShell)**
```powershell
mvn archetype:generate `
  -DarchetypeGroupId="org.eclipse.starter" `
  -DarchetypeArtifactId="jakarta-starter" `
  -DarchetypeVersion="2.6.0" `
  -DjakartaVersion="11" `
  -DgroupId="com.tuempresa.proyecto" `
  -DartifactId="project-manager" `
  -Dpackage="com.tuempresa.proyecto" `
  -DinteractiveMode=false
  
 ```

Este comando creará una carpeta `project-manager`  con la estructura básica de un proyecto web Jakarta EE.

**Importar el Proyecto a tu IDE:**

*Eclipse:*
1.  Ve a File > Import....
2. Selecciona Maven > Existing Maven Projects.
3.  Navega hasta la carpeta `project-manager` que Maven creó y selecciona esa carpeta.
4. Haz clic en Finish.

*NetBeans:*
1. Ve a File > Open Project....
2. Navega hasta la carpeta `project-manager` y selecciona el archivo `pom.xml`.
3. Haz clic en Open Project.

## 8. Desplegar y Ejecutar "Hello World" en GlassFish

Una vez que el proyecto esté importado, es hora de desplegarlo en GlassFish.

**En Eclipse:**
1. En la vista Servers, haz clic derecho en tu servidor GlassFish y selecciona Add and Remove....
2. Selecciona tu proyecto project-manager-pm en la lista de la izquierda y haz clic en Add >.
3. Haz clic en Finish. Eclipse desplegará automáticamente la aplicación.
4. Abre un navegador web y ve a [http://localhost:8080/project-manager/rest/hello](http://localhost:8080/project-manager/rest/hello). Deberías ver un mensaje:
 ```json
{
  "hello": "world"
}
``` 

**En NetBeans:**
1. Haz clic derecho en tu proyecto `project-manager` en la ventana Projects.
2. Selecciona Run. NetBeans desplegará el proyecto en el servidor GlassFish que configuraste y abrirá la URL en tu navegador.
3. Abre un navegador web y ve a [http://localhost:8080/project-manager/rest/hello](http://localhost:8080/project-manager/rest/hello). Deberías ver un mensaje:
 ```json
{
  "hello": "world"
}
``` 

## 9.Vídeo demostrativo


[![Watch the video](https://img.youtube.com/vi/hkt4kCL-LiA/default.jpg)](https://youtu.be/hkt4kCL-LiA)


---
¡Felicidades! Has configurado tu entorno completo y has desplegado tu primera aplicación Jakarta EE en GlassFish. Este es un gran paso.

Si encuentras algún problema en el camino, no dudes en preguntar.

Mañana, en el Día 2, nos sumergiremos en el Diseño de Entidades y JPA Básico para nuestro Sistema de Gestión de Proyectos.

¿Todo bien hasta ahora? ¿Pudiste ver el "Hello, World!"?