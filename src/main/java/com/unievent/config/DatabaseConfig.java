package com.unievent.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class DatabaseConfig implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            Connection connection = dataSource.getConnection();
            if (connection != null) {
                connection.close();
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "Available")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MySQL")
                    .withDetail("error", e.getMessage())
                    .withDetail("status", "Unavailable - Please check MySQL server")
                    .build();
        }
        return Health.down().withDetail("database", "MySQL").build();
    }
}
