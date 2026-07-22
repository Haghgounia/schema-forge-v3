package com.behsazan.schemaforge.configuration.properties;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
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
        @Valid SpellCheck spellCheck,
        Map<String, OracleSchema> oracleSchemas) {

    public SchemaForgeProperties {
        generation = generation == null ? new Generation(DatabaseType.ORACLE, true, true, true) : generation;
        workspace = workspace == null ? new Workspace(Path.of("./work"), true) : workspace;
        web = web == null ? new Web("http://localhost:5173") : web;
        spellCheck = spellCheck == null ? SpellCheck.defaults() : spellCheck;
        oracleSchemas = oracleSchemas == null ? Map.of() : Map.copyOf(new LinkedHashMap<>(oracleSchemas));
    }

    public record Generation(
            @NotNull DatabaseType defaultDatabase,
            boolean includeRollback,
            boolean includeComments,
            boolean includeDbaHints) {}

    public record Workspace(@NotNull Path root, boolean cleanupOnStartup) {}

    public record Web(@NotNull String allowedOrigin) {}

    public record SpellCheck(
            boolean enabled,
            @NotNull String endpoint,
            @NotNull String language,
            @NotNull Duration connectTimeout,
            @NotNull Duration requestTimeout,
            int maximumSuggestions,
            boolean failOpen,
            @NotNull Duration cacheTtl,
            List<String> technicalTerms) {

        public SpellCheck {
            maximumSuggestions = Math.max(0, maximumSuggestions);
            technicalTerms = technicalTerms == null ? List.of() : List.copyOf(technicalTerms);
        }

        public static SpellCheck defaults() {
            return new SpellCheck(
                    false,
                    "https://api.languagetool.org/v2/check",
                    "en-US",
                    Duration.ofSeconds(3),
                    Duration.ofSeconds(5),
                    3,
                    true,
                    Duration.ofHours(24),
                    List.of("ID", "UUID", "IBAN", "SWIFT", "CIF", "GL", "FK", "PK"));
        }
    }

    public record OracleSchema(String dataTablespace, String indexTablespace) {}
}
