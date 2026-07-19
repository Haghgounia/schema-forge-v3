package com.behsazan.schemaforge.configuration.properties;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "schemaforge")
public record SchemaForgeProperties(
        @Valid Generation generation,
        @Valid Workspace workspace,
        @Valid Web web,
        Map<String, OracleSchema> oracleSchemas) {

    public SchemaForgeProperties {
        generation = generation == null ? new Generation(DatabaseType.ORACLE, true, true, true) : generation;
        workspace = workspace == null ? new Workspace(Path.of("./work"), true) : workspace;
        web = web == null ? new Web("http://localhost:5173") : web;
        oracleSchemas = oracleSchemas == null ? Map.of() : Map.copyOf(new LinkedHashMap<>(oracleSchemas));
    }

    public record Generation(
            @NotNull DatabaseType defaultDatabase,
            boolean includeRollback,
            boolean includeComments,
            boolean includeDbaHints) {}

    public record Workspace(@NotNull Path root, boolean cleanupOnStartup) {}

    public record Web(@NotNull String allowedOrigin) {}

    public record OracleSchema(String dataTablespace, String indexTablespace) {}
}
