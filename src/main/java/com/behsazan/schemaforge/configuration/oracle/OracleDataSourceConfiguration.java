package com.behsazan.schemaforge.configuration.oracle;

import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ConditionalOnProperty(prefix = "schemaforge.oracle", name = "enabled", havingValue = "true")
public class OracleDataSourceConfiguration {

    @Bean
    DataSource oracleDataSource(OracleConnectionProperties properties) {
        requireText(properties.url(), "schemaforge.oracle.url");
        requireText(properties.username(), "schemaforge.oracle.username");
        requireText(properties.password(), "schemaforge.oracle.password");

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl(properties.url());
        dataSource.setUsername(properties.username());
        dataSource.setPassword(properties.password());
        return dataSource;
    }

    @Bean
    NamedParameterJdbcTemplate oracleJdbcTemplate(
            DataSource oracleDataSource,
            OracleConnectionProperties properties) {
        NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(oracleDataSource);
        template.getJdbcTemplate().setQueryTimeout(properties.queryTimeoutSeconds());
        return template;
    }

    private void requireText(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " is required when Oracle integration is enabled");
        }
    }
}
