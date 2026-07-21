package com.behsazan.schemaforge.generation.core;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DialectRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.schema.SchemaScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.script.TableScriptGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGeneratorRegistry;
import com.behsazan.schemaforge.generation.ddl.generator.table.TableDdlGenerator;
import com.behsazan.schemaforge.generation.ddl.model.DdlScript;
import com.behsazan.schemaforge.generation.ddl.model.RenderContext;
import com.behsazan.schemaforge.generation.ddl.renderer.DdlRenderer;
import com.behsazan.schemaforge.generation.ddl.renderer.RenderedDdl;
import com.behsazan.schemaforge.generation.ddl.renderer.RendererRegistry;
import java.util.Objects;

/** Vendor-neutral orchestration layer for DDL generation. */
public final class DdlGenerationEngine {
    private final DialectRegistry dialectRegistry;
    private final RendererRegistry rendererRegistry;
    private final ColumnDefinitionGeneratorRegistry columnGeneratorRegistry;

    public DdlGenerationEngine(
            DialectRegistry dialectRegistry,
            RendererRegistry rendererRegistry,
            ColumnDefinitionGeneratorRegistry columnGeneratorRegistry) {
        this.dialectRegistry = Objects.requireNonNull(dialectRegistry, "dialectRegistry must not be null");
        this.rendererRegistry = Objects.requireNonNull(rendererRegistry, "rendererRegistry must not be null");
        this.columnGeneratorRegistry = Objects.requireNonNull(
                columnGeneratorRegistry, "columnGeneratorRegistry must not be null");
    }

    public DdlGenerationResult generate(DdlGenerationRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        DatabaseDialect dialect = dialectRegistry.require(request.databaseProduct());
        DdlRenderer renderer = rendererRegistry.require(request.databaseProduct());
        ColumnDefinitionGenerator columnGenerator =
                columnGeneratorRegistry.require(request.databaseProduct());

        SchemaScriptGenerator scriptGenerator = new SchemaScriptGenerator(
                new TableScriptGenerator(new TableDdlGenerator(columnGenerator)));
        DdlScript script = scriptGenerator.generate(request.schema(), dialect);
        RenderedDdl rendered = renderer.render(
                script,
                new RenderContext(dialect, request.options(), request.clock(), request.attributes()));

        return new DdlGenerationResult(request.databaseProduct(), script, rendered);
    }
}
