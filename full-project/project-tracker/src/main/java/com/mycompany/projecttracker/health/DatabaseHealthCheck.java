package com.mycompany.projecttracker.health;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Readiness health check that verifies database connectivity.
 */
@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    /** Application datasource used to validate connectivity. */
    @Resource(lookup = "jdbc/projectTracker")
    private DataSource ds;

    /**
     * Executes the database readiness probe.
     *
     * @return health response with the database status
     */
    @Override
    public HealthCheckResponse call() {

        /*
         * Se abre una conexión corta y se valida con timeout para evitar que el health check
         * quede bloqueado demasiado tiempo si la base de datos no responde.
         */
        try (Connection conn = ds.getConnection()) {
            if (conn.isValid(2)) {
                return HealthCheckResponse.named("Database Connection")
                    .up()
                    .withData("database", "PostgreSQL at Docket")
                    .build();
            } else {
                return HealthCheckResponse.named("Database Connection")
                    .down()
                    .withData("error", "Conexión inválida")
                    .build();
            }
        } catch (Exception e) {
            return HealthCheckResponse.named("Database Connection")
                .down()
                .withData("error", e.getMessage())
                .build();
        }
    }
}
