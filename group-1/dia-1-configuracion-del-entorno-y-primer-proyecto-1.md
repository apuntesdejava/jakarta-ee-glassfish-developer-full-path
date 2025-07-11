# Día 1: Configuración del Entorno y Primer Proyecto 1

Hoy vamos a sentar las bases para todo el camino que tenemos por delante. Lo primero es asegurarnos de que tienes todo lo necesario para empezar a desarrollar con [Jakarta EE](https://jakarta.ee/) y [GlassFish](https://glassfish.org/).

### 1. Requisitos Previos

Antes de instalar cualquier cosa, asegúrate de tener estos elementos listos:

* Conexión a Internet Estable: Necesitarás descargar varios archivos grandes.
* Espacio en Disco Suficiente: Los IDEs, servidores de aplicaciones y SDKs pueden ocupar varios GB.

### 2. Java Development Kit (JDK)

Jakarta EE 10 requiere al menos de JDK 11 o superior. Te recomiendo usar la última versión LTS disponible, que actualmente es la JDK 21.

* Descarga. Puedes descargar el JDK desde los siguientes sitios oficiales:
*
  * [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) (requiere una cuenta de Oracle)
  * [OpenJDK](https://www.google.com/search?q=https://openjdk.java.net/install/index.html) (varias distribuciones como Adoptium Temurin, Amazon Corretto, Azul Zulu). Te recomiendo Adoptium Temurin por ser de código abierto y fácil de usar:[ https://adoptium.net/temurin/releases/](https://adoptium.net/temurin/releases/)&#x20;
  * Instalación: Sigue las instrucciones para tu sistema operativo (Windows, macOS, Linux).
  * Verificación: Abre una terminal o Símbolo del sistema y ejecuta:\
    java -version\
    javac -version
  * Ambos comandos deberían mostrar la versión del JDK que acabas de instalar.

### 3. Entorno de Desarrollo Integrado (IDE)

Para desarrollar aplicaciones Jakarta EE, un buen IDE es fundamental. Mis recomendaciones son:

* IntelliJ IDEA Ultimate Edition: Es el más potente y con mejor soporte para Jakarta EE, pero es de pago. Ofrecen una prueba gratuita pero no incluye el soporte para Jakarta EE.
* Eclipse IDE for Enterprise Java and Web Developers: Una opción gratuita y muy popular.
* NetBeans IDE: También gratuito y con excelente integración con GlassFish.
* Visual Studio Code: Un editor de código muy conocido, pero la configuración para desarrollar en Jakarta EE es un poco complicado.

Recomendación para este tutorial: Si no tienes una preferencia fuerte o si es tu primera vez, te sugiero Eclipse IDE for Enterprise Java and Web Developers o NetBeans IDE. Ambos son excelentes y gratuitos.

* Descarga e Instalación:
*
  * Eclipse: Ve a[ https://www.eclipse.org/downloads/packages/](https://www.eclipse.org/downloads/packages/) y descarga la versión "Eclipse IDE for Enterprise Java and Web Developers".\
    \
    Descomprime el archivo y ejecuta el ejecutable de Eclipse.

Apache NetBeans: Ve a[ https://netbeans.apache.org/download/index.html](https://netbeans.apache.org/download/index.html) y descarga la última versión.  Descomprime el archivo y ejecuta el ejecutable que está en "bin\netbeans"

### 4. Servidor de Aplicaciones GlassFish

GlassFish es nuestro servidor de aplicaciones. Usaremos la versión más reciente que soporta Jakarta EE 10.

Descarga: Ve al sitio de GlassFish:[ ](https://eclipse-ee4j.github.io/glassfish/download)https://glassfish.org/download

* Busca la versión estable más reciente (ej. GlassFish 7.0.x para Jakarta EE 10).
* Descarga el archivo ZIP o TAR.GZ.

Instalación:

* Descomprime el archivo descargado en una ubicación de tu disco donde tengas permisos de escritura y no haya espacios en el nombre de la carpeta (ej. C:\glassfish7 en Windows, o \~/glassfish7 en Linux/macOS).
* ¡Importante! Anota esta ruta, la usaremos más adelante.

### 5. Apache Maven (Sistema de Gestión de Proyectos)

Maven es la herramienta estándar para construir y gestionar proyectos Java.

Descarga: Ve a[ https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

* Descarga el archivo binario (apache-maven-X.Y.Z-bin.zip o .tar.gz).

Instalación:

* Descomprime el archivo en una ubicación de tu disco (ej. C:\apache-maven-3.9.10 o \~/apache-maven-3.9.10).
* Configuración de Variables de Entorno:
*
  * Añade la ruta del directorio bin de Maven a la variable de entorno PATH de tu sistema.
  * Crea una variable de entorno MAVEN\_HOME apuntando al directorio raíz de Maven (donde lo descomprimiste).
* Verificación: Abre una nueva terminal y ejecuta:

mvn -v

Debería mostrar la versión de Maven.

### 6. Integración del Servidor GlassFish en el IDE

Ahora que tienes todos los componentes, vamos a integrar GlassFish en tu IDE para facilitar el desarrollo y despliegue.
