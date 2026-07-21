package com.behsazan.schemaforge.generation.enrichment;

import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.domain.valueobject.DataType;
import java.util.Objects;

/** Adds the standard audit columns to the end of every table when absent. */
public final class AuditColumnSchemaEnricher implements SchemaEnricher {
    public static final String CREATED_BY = "CREATED_BY";
    public static final String CREATED_DATE = "CREATED_DATE";
    public static final String LAST_MODIFIED_BY = "LAST_MODIFIED_BY";
    public static final String LAST_MODIFIED_DATE = "LAST_MODIFIED_DATE";

    @Override
    public DatabaseSchema enrich(DatabaseSchema schema) {
        Objects.requireNonNull(schema, "schema must not be null");

        DatabaseSchema.Builder result = DatabaseSchema.builder(schema.name().value())
                .description(schema.description().value());
        schema.metadata().forEach(result::metadata);
        schema.tables().stream().map(this::enrichTable).forEach(result::addTable);
        schema.sequences().forEach(result::addSequence);
        schema.views().forEach(result::addView);
        schema.synonyms().forEach(result::addSynonym);
        schema.triggers().forEach(result::addTrigger);
        schema.routines().forEach(result::addRoutine);
        schema.grants().forEach(result::addGrant);
        return result.build();
    }

    private Table enrichTable(Table table) {
        String schemaName = table.qualifiedName().schemaName().map(value -> value.value()).orElse(null);
        Table.Builder result = Table.builder(schemaName, table.qualifiedName().name().value())
                .description(table.description().value());

        table.columns().forEach(result::addColumn);
        addIfMissing(table, result, Column.required(CREATED_BY, DataType.varchar("VARCHAR", 50)));
        addIfMissing(table, result, Column.required(CREATED_DATE, DataType.simple("TIMESTAMP")));
        addIfMissing(table, result, Column.nullable(LAST_MODIFIED_BY, DataType.varchar("VARCHAR", 50)));
        addIfMissing(table, result, Column.nullable(LAST_MODIFIED_DATE, DataType.simple("TIMESTAMP")));

        table.primaryKey().ifPresent(result::primaryKey);
        table.foreignKeys().forEach(result::addForeignKey);
        table.uniqueKeys().forEach(result::addUniqueKey);
        table.checkConstraints().forEach(result::addCheck);
        table.indexes().forEach(result::addIndex);
        table.physicalOptions().forEach(result::physicalOption);
        return result.build();
    }

    private void addIfMissing(Table source, Table.Builder target, Column column) {
        if (source.findColumn(column.name().value()).isEmpty()) {
            target.addColumn(column);
        }
    }
}
