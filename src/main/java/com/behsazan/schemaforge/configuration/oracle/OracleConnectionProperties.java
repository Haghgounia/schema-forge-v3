package com.behsazan.schemaforge.configuration.oracle;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "schemaforge.oracle")
public record OracleConnectionProperties(
        boolean enabled,
        String url,
        String username,
        String password,
        int queryTimeoutSeconds) {

    public OracleConnectionProperties {
        queryTimeoutSeconds = queryTimeoutSeconds <= 0 ? 60 : queryTimeoutSeconds;
    }
}
