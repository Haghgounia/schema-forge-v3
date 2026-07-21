package com.behsazan.schemaforge.configuration.postgresql;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schemaforge.postgresql")
public record PostgreSqlConnectionProperties(
        boolean enabled,
        String url,
        String username,
        String password,
        int queryTimeoutSeconds) {
    public PostgreSqlConnectionProperties {
        queryTimeoutSeconds = queryTimeoutSeconds <= 0 ? 60 : queryTimeoutSeconds;
    }
}
