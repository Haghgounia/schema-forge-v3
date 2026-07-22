package com.behsazan.schemaforge.application.database;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.Map;
import java.util.Optional;

/**
 * DBMS-neutral port for reading the live metadata of one table.
 * Vendor-specific SQL and catalog names must remain in adapter implementations.
 */
public interface DatabaseMetadataReader {
    DatabaseProduct databaseProduct();

    Optional<Table> readTable(String schemaName, String tableName);

    default boolean tableExists(String schemaName, String tableName) {
        return readTable(schemaName, tableName).isPresent();
    }

    default Map<String, Integer> columnUsageCounts() {
        return Map.of();
    }
}
