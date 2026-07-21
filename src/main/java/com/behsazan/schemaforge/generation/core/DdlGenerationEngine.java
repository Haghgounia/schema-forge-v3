package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.dialect.DialectRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.schema.SchemaScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.script.TableScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGeneratorRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.renderer.RendererRegistry;
import com.behsazan.schemaforge.generation.ddl.renderer.RenderedDdl;
import com.behsazan.schemaforge.generation.plugin.DatabaseDdlPlugin;
import com.behsazan.schemaforge.generation.plugin.DatabaseDdlPluginRegistry;
import com.behsazan.schemaforge.generation.plugin.DefaultDatabaseDdlPlugin;
import java.util.ArrayList;
import java.util.Objects;

/** Vendor-neutral orchestration layer for DDL generation. */
public final class DdlGenerationEngine {
    private final DatabaseDdlPluginRegistry pluginRegistry;

    public DdlGenerationEngine(DatabaseDdlPluginRegistry pluginRegistry) {
        this.pluginRegistry = Objects.requireNonNull(pluginRegistry, "pluginRegistry must not be null");
    }

    /**
     * @deprecated Use {@link #DdlGenerationEngine(DatabaseDdlPluginRegistry)}.
     * Scheduled for removal in Phase 3.6.
     */
    @Deprecated(forRemoval = true, since = "3.4")
    public DdlGenerationEngine(
            DialectRegistry dialectRegistry,
            RendererRegistry rendererRegistry,
            ColumnDefinitionGeneratorRegistry columnGeneratorRegistry) {
        Objects.requireNonNull(dialectRegistry, "dialectRegistry must not be null");
        Objects.requireNonNull(rendererRegistry, "rendererRegistry must not be null");
        Objects.requireNonNull(columnGeneratorRegistry, "columnGeneratorRegistry must not be null");
        var plugins = new ArrayList<DatabaseDdlPlugin>();
        for (var product : dialectRegistry.products()) {
            plugins.add(new DefaultDatabaseDdlPlugin(
                    dialectRegistry.require(product),
                    rendererRegistry.require(product),
                    columnGeneratorRegistry.require(product)));
        }
        this.pluginRegistry = new DatabaseDdlPluginRegistry(plugins);
    }

    public DdlGenerationResult generate(DdlGenerationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        DatabaseDdlPlugin plugin = pluginRegistry.require(request.databaseProduct());
        SchemaScriptGenerator scriptGenerator = new SchemaScriptGenerator(
                new TableScriptGenerator(new TableDdlGenerator(plugin.columnDefinitionGenerator())));
        DdlScript script = scriptGenerator.generate(request.schema(), plugin.dialect());
        RenderedDdl rendered = plugin.renderer().render(
                script,
                new RenderContext(plugin.dialect(), request.options(), request.clock(), request.attributes()));
        return new DdlGenerationResult(request.databaseProduct(), script, rendered);
    }
}
