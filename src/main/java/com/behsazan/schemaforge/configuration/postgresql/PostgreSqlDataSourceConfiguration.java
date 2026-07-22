package com.behsazan.schemaforge.configuration.postgresql;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ConditionalOnProperty(prefix = "schemaforge.postgresql", name = "enabled", havingValue = "true")
@ConditionalOnExpression("T(org.springframework.util.StringUtils).hasText(\'${schemaforge.postgresql.url:}\')")
public class PostgreSqlDataSourceConfiguration {

    @Bean("postgresqlDataSource")
    DataSource postgresqlDataSource(PostgreSqlConnectionProperties properties) {
        requireText(properties.url(), "schemaforge.postgresql.url");
        requireText(properties.username(), "schemaforge.postgresql.username");
        requireText(properties.password(), "schemaforge.postgresql.password");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(properties.url());
        dataSource.setUsername(properties.username());
        dataSource.setPassword(properties.password());
        return dataSource;
    }

    @Bean("postgresqlJdbcTemplate")
    NamedParameterJdbcTemplate postgresqlJdbcTemplate(
            @Qualifier("postgresqlDataSource") DataSource dataSource,
            PostgreSqlConnectionProperties properties) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
        template.getJdbcTemplate().setQueryTimeout(properties.queryTimeoutSeconds());
        return template;
    }

    private static void requireText(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " is required when PostgreSQL integration is enabled");
        }
    }
}
