package com.mycompany.projecttracker.adapter.in.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        try (Connection conn = dataSource().getConnection()) {
            if (conn.isValid(2)) {
                return HealthCheckResponse.named("Database Connection")
                    .up()
                    .withData("database", "MySQL at Docker")
                    .build();
            }

            return HealthCheckResponse.named("Database Connection")
                .down()
                .withData("error", "Conexión inválida")
                .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("Database Connection")
                .down()
                .withData("error", e.getMessage())
                .build();
        }
    }

    private DataSource dataSource() throws Exception {
        return (DataSource) new InitialContext().lookup("jdbc/projectTracker");
    }
}
