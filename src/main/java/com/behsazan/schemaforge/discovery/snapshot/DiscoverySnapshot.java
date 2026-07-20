package com.behsazan.schemaforge.discovery.snapshot;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class DiscoverySnapshot {

    private final TableDefinition documentTable;
    private final DatabaseSchema databaseSchema;
    private final Map<String, List<ColumnUsage>> columnUsageByName;

    DiscoverySnapshot(
            TableDefinition documentTable,
            DatabaseSchema databaseSchema,
            Map<String, List<ColumnUsage>> columnUsageByName) {
        this.documentTable = Objects.requireNonNull(documentTable, "documentTable must not be null");
        this.databaseSchema = Objects.requireNonNull(databaseSchema, "databaseSchema must not be null");
        this.columnUsageByName = Map.copyOf(Objects.requireNonNull(columnUsageByName, "columnUsageByName must not be null"));
    }

    public TableDefinition documentTable() {
        return documentTable;
    }

    public DatabaseSchema databaseSchema() {
        return databaseSchema;
    }

    public List<ColumnUsage> findColumnUsage(String columnName) {
        if (columnName == null || columnName.isBlank()) {
            return List.of();
        }
        return columnUsageByName.getOrDefault(normalize(columnName), List.of());
    }

    public Map<String, List<ColumnUsage>> columnUsageByName() {
        return columnUsageByName;
    }

    static String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
