package com.behsazan.schemaforge.database.spi;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.domain.DatabaseDictionary;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Vendor-neutral SPI for database dictionary information used by validation.
 * Each DBMS adapter supplies its own SQL and mapping implementation.
 */
public interface DatabaseDictionaryProvider {

    DatabaseType databaseType();

    default Set<String> loadReservedWords() {
        return Set.of();
    }

    default Map<String, Integer> loadColumnUsageCounts() {
        return Map.of();
    }

    default Map<String, List<ColumnDataTypeUsage>> loadColumnDataTypeUsages() {
        return Map.of();
    }

    default DatabaseDictionary loadDictionary() {
        return new DatabaseDictionary(
                databaseType(),
                true,
                loadReservedWords(),
                loadColumnUsageCounts(),
                loadColumnDataTypeUsages());
    }
}
