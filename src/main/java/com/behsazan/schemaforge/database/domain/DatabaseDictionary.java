package com.behsazan.schemaforge.database.domain;

import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Immutable, DBMS-neutral snapshot of dictionary information used by
 * validation and DDL generation.
 */
public record DatabaseDictionary(
        DatabaseType databaseType,
        boolean available,
        Set<String> reservedWords,
        Map<String, Integer> columnUsageCounts,
        Map<String, List<ColumnDataTypeUsage>> columnDataTypeUsages) {

    public DatabaseDictionary {
        if (databaseType == null) {
            throw new IllegalArgumentException("databaseType must not be null");
        }
        reservedWords = reservedWords == null
                ? Set.of()
                : reservedWords.stream()
                        .filter(value -> value != null && !value.isBlank())
                        .map(DatabaseDictionary::normalize)
                        .collect(Collectors.toUnmodifiableSet());
        columnUsageCounts = columnUsageCounts == null
                ? Map.of()
                : columnUsageCounts.entrySet().stream()
                        .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank())
                        .collect(Collectors.toUnmodifiableMap(
                                entry -> normalize(entry.getKey()),
                                entry -> entry.getValue() == null ? 0 : Math.max(0, entry.getValue()),
                                Math::max));
        columnDataTypeUsages = columnDataTypeUsages == null
                ? Map.of()
                : columnDataTypeUsages.entrySet().stream()
                        .filter(entry -> entry.getKey() != null && !entry.getKey().isBlank())
                        .collect(Collectors.toUnmodifiableMap(
                                entry -> normalize(entry.getKey()),
                                entry -> entry.getValue() == null ? List.of() : List.copyOf(entry.getValue()),
                                (left, right) -> left));
    }

    public static DatabaseDictionary empty(DatabaseType databaseType) {
        return new DatabaseDictionary(databaseType, false, Set.of(), Map.of(), Map.of());
    }

    public boolean isReservedWord(String identifier) {
        return identifier != null && !identifier.isBlank() && reservedWords.contains(normalize(identifier));
    }

    public int columnUsageCount(String columnName) {
        return columnName == null || columnName.isBlank()
                ? 0
                : columnUsageCounts.getOrDefault(normalize(columnName), 0);
    }

    public List<ColumnDataTypeUsage> columnDataTypeUsages(String columnName) {
        return columnName == null || columnName.isBlank()
                ? List.of()
                : columnDataTypeUsages.getOrDefault(normalize(columnName), List.of());
    }

    private static String normalize(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }
}
