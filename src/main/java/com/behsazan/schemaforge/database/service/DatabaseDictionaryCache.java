package com.behsazan.schemaforge.database.service;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.domain.DatabaseDictionary;
import com.behsazan.schemaforge.database.spi.DatabaseDictionaryProvider;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Shared cache for all configured DBMS dictionary providers.
 * A failed database connection produces an unavailable empty snapshot; callers
 * therefore receive zero usage counts and empty validation collections.
 */
@Component
public class DatabaseDictionaryCache {

    private static final Logger log = LoggerFactory.getLogger(DatabaseDictionaryCache.class);

    private final Map<DatabaseType, DatabaseDictionaryProvider> providers;
    private final Map<DatabaseType, DatabaseDictionary> snapshots = new ConcurrentHashMap<>();

    @Autowired
    public DatabaseDictionaryCache(List<DatabaseDictionaryProvider> providers) {
        Map<DatabaseType, DatabaseDictionaryProvider> byType = new EnumMap<>(DatabaseType.class);
        for (DatabaseDictionaryProvider provider : providers) {
            Objects.requireNonNull(provider, "database dictionary provider must not be null");
            DatabaseType databaseType = Objects.requireNonNull(
                    provider.databaseType(),
                    () -> "databaseType must not be null for provider " + provider.getClass().getName());
            DatabaseDictionaryProvider previous = byType.put(databaseType, provider);
            if (previous != null) {
                throw new IllegalStateException(
                        "More than one database dictionary provider is configured for " + databaseType);
            }
        }
        this.providers = Map.copyOf(byType);
    }

    /** Convenience constructor for unit tests and non-Spring usage. */
    public DatabaseDictionaryCache(DatabaseDictionaryProvider provider) {
        this(List.of(Objects.requireNonNull(provider, "provider must not be null")));
    }

    @PostConstruct
    public void initialize() {
        providers.keySet().forEach(this::refresh);
    }

    public DatabaseDictionary dictionary(DatabaseType databaseType) {
        Objects.requireNonNull(databaseType, "databaseType must not be null");
        return snapshots.getOrDefault(databaseType, DatabaseDictionary.empty(databaseType));
    }

    public void refresh(DatabaseType databaseType) {
        DatabaseDictionaryProvider provider = providers.get(databaseType);
        if (provider == null) {
            snapshots.put(databaseType, DatabaseDictionary.empty(databaseType));
            return;
        }
        Set<String> reservedWords = safelyLoad(
                databaseType,
                "reserved words",
                provider::loadReservedWords,
                Set.<String>of());
        Map<String, Integer> usageCounts = safelyLoad(
                databaseType,
                "column usage counts",
                provider::loadColumnUsageCounts,
                Map.<String, Integer>of());
        Map<String, List<ColumnDataTypeUsage>> dataTypeUsages = safelyLoad(
                databaseType,
                "column datatype usages",
                provider::loadColumnDataTypeUsages,
                Map.<String, List<ColumnDataTypeUsage>>of());
        boolean available = !(reservedWords.isEmpty() && usageCounts.isEmpty() && dataTypeUsages.isEmpty());
        snapshots.put(databaseType, new DatabaseDictionary(
                databaseType, available, reservedWords, usageCounts, dataTypeUsages));
    }

    private <T> T safelyLoad(
            DatabaseType databaseType,
            String metadataName,
            java.util.function.Supplier<T> loader,
            T fallback) {
        try {
            T value = loader.get();
            return value == null ? fallback : value;
        } catch (RuntimeException exception) {
            log.warn("{} {} could not be loaded; fallback metadata will be used",
                    databaseType, metadataName, exception);
            return fallback;
        }
    }

    public boolean isReservedWord(DatabaseType databaseType, String identifier) {
        return dictionary(databaseType).isReservedWord(identifier);
    }

    public int getColumnUsageCount(DatabaseType databaseType, String columnName) {
        return dictionary(databaseType).columnUsageCount(columnName);
    }

    public List<ColumnDataTypeUsage> getColumnDataTypeUsages(
            DatabaseType databaseType,
            String columnName) {
        return dictionary(databaseType).columnDataTypeUsages(columnName);
    }
}
