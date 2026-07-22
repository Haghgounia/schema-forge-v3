package com.behsazan.schemaforge.application.database;

import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.Map;
import java.util.Optional;

/** DBMS-neutral access to one live table used by artifact routing and comparison. */
public interface DatabaseTableLookup {
    DatabaseProduct databaseProduct();

    Optional<Table> findTable(String schemaName, String tableName);

    default Map<String, Integer> columnUsageCounts() {
        return Map.of();
    }
}
