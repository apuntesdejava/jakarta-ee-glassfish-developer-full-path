
1.  **Crear el Pool de Conexiones:**
 
    \
    **Linux/macOS**
     ```shell
    ./asadmin create-jdbc-connection-pool \
        --datasourceclassname=com.mysql.cj.jdbc.MysqlDataSource \
        --restype=javax.sql.DataSource \
        --ping=true \
        --property="URL=jdbc\:mysql\://localhost\:3306/PROJECT_TRACKER:user=PROJECT_TRACKER:password=PROJECT_TRACKER:useSSL=false:serverTimezone=America\/Lima:allowPublicKeyRetrieval=true" \
        ProjectTrackerPool
    ```
    \
    **Windows**
     ```powershell
    .\asadmin create-jdbc-connection-pool `
        --datasourceclassname="com.mysql.cj.jdbc.MysqlDataSource" `
        --restype="javax.sql.DataSource" `
        --ping=true `
        --property="URL=jdbc\:mysql\://localhost\:3306/PROJECT_TRACKER:user=PROJECT_TRACKER:password=PROJECT_TRACKER:useSSL=false:serverTimezone=America\/Lima:allowPublicKeyRetrieval=true" `
        ProjectTrackerPool
    ```

    *Esto crea un pool llamado `ProjectTrackerPool` que apunta a la base de datos que hemos creado en PostgreSQL.*
 
    > Para más información de las propiedades de JDBC de PosgreSQL, revisar la siguiente documentación: https://jdbc.postgresql.org/documentation/use/ 

2.  **Crear el Recurso JNDI:**

    \
    **Linux/macOS**
     ```shell
    ./asadmin create-jdbc-resource \
        --connectionpoolid ProjectTrackerPool \
        jdbc/projectTracker
    ```

    \
    **Windows**
     ```powershell
    .\asadmin create-jdbc-resource `
        --connectionpoolid ProjectTrackerPool `
        jdbc/projectTracker
    ```
