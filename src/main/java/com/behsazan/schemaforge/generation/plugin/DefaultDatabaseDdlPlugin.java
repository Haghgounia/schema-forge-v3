package com.behsazan.schemaforge.generation.plugin;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.generation.ddl.generator.table.ColumnDefinitionGenerator;
import com.behsazan.schemaforge.generation.ddl.renderer.DdlRenderer;
import java.util.Objects;

public final class DefaultDatabaseDdlPlugin implements DatabaseDdlPlugin {
    private final DatabaseDialect dialect;
    private final DdlRenderer renderer;
    private final ColumnDefinitionGenerator columnDefinitionGenerator;

    public DefaultDatabaseDdlPlugin(
            DatabaseDialect dialect,
            DdlRenderer renderer,
            ColumnDefinitionGenerator columnDefinitionGenerator) {
        this.dialect = Objects.requireNonNull(dialect, "dialect must not be null");
        this.renderer = Objects.requireNonNull(renderer, "renderer must not be null");
        this.columnDefinitionGenerator = Objects.requireNonNull(
                columnDefinitionGenerator, "columnDefinitionGenerator must not be null");
        DatabaseProduct product = dialect.product();
        if (renderer.product() != product || columnDefinitionGenerator.product() != product) {
            throw new IllegalArgumentException("DDL plugin components must target the same database product");
        }
    }

    @Override public DatabaseProduct product() { return dialect.product(); }
    @Override public DatabaseDialect dialect() { return dialect; }
    @Override public DdlRenderer renderer() { return renderer; }
    @Override public ColumnDefinitionGenerator columnDefinitionGenerator() { return columnDefinitionGenerator; }
}
