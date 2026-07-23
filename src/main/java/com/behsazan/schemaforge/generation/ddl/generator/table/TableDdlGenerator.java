package com.behsazan.schemaforge.generation.ddl.generator.table;

import com.behsazan.schemaforge.dialect.DatabaseDialect;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.generation.ddl.model.DdlObjectReference;
import com.behsazan.schemaforge.generation.ddl.model.DdlPhase;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatement;
import com.behsazan.schemaforge.generation.ddl.model.DdlStatementType;
import com.behsazan.schemaforge.generation.ddl.model.SqlFragment;
import com.behsazan.schemaforge.generation.ddl.model.StatementOrder;
import com.behsazan.schemaforge.generation.ddl.generator.storage.PhysicalOptionsRenderer;
import java.util.Objects;
import java.util.stream.Collectors;

public final class TableDdlGenerator {
    private final ColumnDefinitionGenerator columnGenerator;
    private final IdentifierSqlRenderer identifierRenderer;
    private final PhysicalOptionsRenderer physicalOptionsRenderer;

    public TableDdlGenerator(ColumnDefinitionGenerator columnGenerator) {
        this.columnGenerator = Objects.requireNonNull(columnGenerator, "columnGenerator must not be null");
        this.identifierRenderer = new IdentifierSqlRenderer();
        this.physicalOptionsRenderer = new PhysicalOptionsRenderer();
    }

    public DdlStatement generate(Table table, DatabaseDialect dialect, int position) {
        Objects.requireNonNull(table, "table must not be null");
        Objects.requireNonNull(dialect, "dialect must not be null");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
        String columns = table.columns().stream()
                .map(column -> "    " + columnGenerator.generate(column, dialect))
                .collect(Collectors.joining(",\n"));
        String physicalOptions = physicalOptionsRenderer.render(table.physicalOptions(), dialect);
        if (dialect.product() == DatabaseProduct.ORACLE
                && table.physicalOptions().keySet().stream().noneMatch(key -> key.equalsIgnoreCase("TABLESPACE"))) {
            String schema = table.qualifiedName().schemaName().map(value -> value.value()).orElse("");
            if (!schema.isBlank()) {
                physicalOptions += "\nTABLESPACE TS_" + schema;
            }
        }
        String sql = "CREATE TABLE " + identifierRenderer.render(table.qualifiedName(), dialect)
                + " (\n" + columns + "\n)"
                + physicalOptions;
        DdlObjectReference reference = new DdlObjectReference(
                table.qualifiedName().schemaName().map(Object::toString).orElse(""),
                table.qualifiedName().name().toString(),
                "TABLE");
        return DdlStatement.of(
                DdlStatementType.CREATE_TABLE,
                reference,
                new StatementOrder(DdlPhase.TABLES, position),
                SqlFragment.of(sql));
    }
}
