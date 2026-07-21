package com.behsazan.schemaforge.configuration;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DialectRegistry;
import com.behsazan.schemaforge.dialect.oracle.OracleDialect;
import com.behsazan.schemaforge.dialect.postgresql.PostgreSqlDialect;
import com.behsazan.schemaforge.generation.core.DdlGenerationEngine;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGeneratorRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.table.oracle.OracleColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.postgresql.PostgreSqlColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.renderer.DdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.RendererRegistry;
import com.behsazan.schemaforge.generation.ddl.renderer.oracle.OracleDdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.postgresql.PostgreSqlDdlRenderer;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring composition root for the vendor-neutral, plugin-based DDL engine. */
@Configuration
public class DdlEngineConfiguration {

    @Bean
    OracleDialect oracleDdlDialect() {
        return new OracleDialect();
    }

    @Bean
    PostgreSqlDialect postgreSqlDdlDialect() {
        return new PostgreSqlDialect();
    }

    @Bean
    DialectRegistry ddlDialectRegistry(List<DatabaseDialect> dialects) {
        return new DialectRegistry(dialects);
    }

    @Bean
    OracleDdlRenderer oracleDdlRenderer() {
        return new OracleDdlRenderer();
    }

    @Bean
    PostgreSqlDdlRenderer postgreSqlDdlRenderer() {
        return new PostgreSqlDdlRenderer();
    }

    @Bean
    RendererRegistry ddlRendererRegistry(List<DdlRenderer> renderers) {
        return new RendererRegistry(renderers);
    }

    @Bean
    OracleColumnDefinitionGenerator oracleColumnDefinitionGenerator() {
        return new OracleColumnDefinitionGenerator();
    }

    @Bean
    PostgreSqlColumnDefinitionGenerator postgreSqlColumnDefinitionGenerator() {
        return new PostgreSqlColumnDefinitionGenerator();
    }

    @Bean
    ColumnDefinitionGeneratorRegistry columnDefinitionGeneratorRegistry(
            List<ColumnDefinitionGenerator> generators) {
        return new ColumnDefinitionGeneratorRegistry(generators);
    }

    @Bean
    DdlGenerationEngine ddlGenerationEngine(
            DialectRegistry dialectRegistry,
            RendererRegistry rendererRegistry,
            ColumnDefinitionGeneratorRegistry columnGeneratorRegistry) {
        return new DdlGenerationEngine(dialectRegistry, rendererRegistry, columnGeneratorRegistry);
    }
}
