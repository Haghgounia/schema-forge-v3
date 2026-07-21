package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.service.DatabaseDictionaryCache;
import com.behsazan.schemaforge.database.spi.DatabaseDictionaryProvider;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @deprecated Use {@link DatabaseDictionaryCache}. This Oracle-only facade is
 * retained temporarily for source compatibility with phase-1/phase-2 callers.
 */
@Deprecated(forRemoval = true)
public class OracleDictionaryCache {

    private final DatabaseDictionaryCache delegate;

    public OracleDictionaryCache(OracleMetadataProvider provider) {
        Objects.requireNonNull(provider, "provider must not be null");
        DatabaseDictionaryProvider oracleAdapter = new DatabaseDictionaryProvider() {
            @Override public DatabaseType databaseType() { return DatabaseType.ORACLE; }
            @Override public Set<String> loadReservedWords() { return provider.loadReservedWords(); }
            @Override public Map<String, Integer> loadColumnUsageCounts() { return provider.loadColumnUsageCounts(); }
            @Override public Map<String, List<ColumnDataTypeUsage>> loadColumnDataTypeUsages() {
                return provider.loadColumnDataTypeUsages();
            }
        };
        this.delegate = new DatabaseDictionaryCache(oracleAdapter);
    }

    public void initialize() { delegate.refresh(DatabaseType.ORACLE); }
    public void refresh() { initialize(); }
    public boolean isReservedWord(String word) { return delegate.isReservedWord(DatabaseType.ORACLE, word); }
    public int getColumnUsageCount(String columnName) { return delegate.getColumnUsageCount(DatabaseType.ORACLE, columnName); }
    public List<ColumnDataTypeUsage> getColumnDataTypeUsages(String columnName) {
        return delegate.getColumnDataTypeUsages(DatabaseType.ORACLE, columnName);
    }
    public DatabaseDictionaryCache delegate() { return delegate; }
}
