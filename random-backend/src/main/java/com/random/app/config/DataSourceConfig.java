package com.random.app.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("prod")
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        String username = System.getenv("DATABASE_USERNAME");
        String password = System.getenv("DATABASE_PASSWORD");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            // Fallback to default
            return DataSourceBuilder.create()
                    .url("jdbc:postgresql://localhost:5432/random_app")
                    .username("postgres")
                    .password("")
                    .build();
        }

        // Convert Render's postgres:// to jdbc:postgresql://
        String jdbcUrl = databaseUrl;
        if (databaseUrl.startsWith("postgres://")) {
            jdbcUrl = "jdbc:postgresql://" + databaseUrl.substring("postgres://".length());
        } else if (databaseUrl.startsWith("postgresql://")) {
            jdbcUrl = "jdbc:postgresql://" + databaseUrl.substring("postgresql://".length());
        }

        // Parse username/password from URL if not provided separately
        if (username == null || username.isBlank()) {
            try {
                String withoutProtocol = jdbcUrl.substring("jdbc:postgresql://".length());
                String userInfo = withoutProtocol.split("@")[0];
                String[] parts = userInfo.split(":");
                if (parts.length >= 1) username = parts[0];
                if (parts.length >= 2) password = parts[1];
                // Remove credentials from URL
                jdbcUrl = "jdbc:postgresql://" + withoutProtocol.substring(userInfo.length() + 1);
            } catch (Exception e) {
                // Use provided values or defaults
            }
        }

        String finalUsername = (username != null && !username.isBlank()) ? username : "postgres";
        String finalPassword = (password != null) ? password : "";

        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(finalUsername)
                .password(finalPassword)
                .build();
    }
}
