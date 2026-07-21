package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.service.DatabaseDictionaryCache;
import com.behsazan.schemaforge.domain.model.Column;
import com.behsazan.schemaforge.generation.spi.DatabaseType;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** Applies target-database dictionary rules to one Oracle output column. */
final class OracleColumnMetadataInspector {

    private final DatabaseDictionaryCache dictionaryCache;
    private final OracleCanonicalTypeMapper typeMapper;

    OracleColumnMetadataInspector(
            DatabaseDictionaryCache dictionaryCache,
            OracleCanonicalTypeMapper typeMapper) {
        this.dictionaryCache = dictionaryCache;
        this.typeMapper = typeMapper;
    }

    int usageCount(Column column) {
        return dictionaryCache == null ? 0
                : dictionaryCache.getColumnUsageCount(DatabaseType.ORACLE, column.name().value());
    }

    boolean isReservedWord(Column column) {
        return dictionaryCache != null
                && dictionaryCache.isReservedWord(DatabaseType.ORACLE, column.name().value());
    }

    DataTypeMismatch dataTypeMismatch(Column column) {
        if (dictionaryCache == null) {
            return null;
        }
        List<ColumnDataTypeUsage> usages = dictionaryCache.getColumnDataTypeUsages(
                DatabaseType.ORACLE, column.name().value());
        if (usages.isEmpty()) {
            return null;
        }
        String documentType = normalizeSignature(typeMapper.map(column.dataType()));
        String databaseType = findStandardType(usages);
        if (databaseType.isBlank() || databaseType.equals(documentType)) {
            return null;
        }
        int usageCount = usages.stream()
                .filter(usage -> databaseType.equals(normalizeSignature(usage.typeSignature())))
                .mapToInt(ColumnDataTypeUsage::usageCount)
                .sum();
        return new DataTypeMismatch(documentType, databaseType, usageCount);
    }

    private String findStandardType(List<ColumnDataTypeUsage> usages) {
        return usages.stream()
                .collect(Collectors.toMap(
                        usage -> normalizeSignature(usage.typeSignature()),
                        ColumnDataTypeUsage::usageCount,
                        Integer::sum))
                .entrySet().stream()
                .max(Comparator.comparingInt((Map.Entry<String, Integer> entry) -> entry.getValue())
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private String normalizeSignature(String value) {
        return value == null ? "" : value.replaceAll("\\s+", "").trim().toUpperCase(Locale.ROOT);
    }

    record DataTypeMismatch(String documentType, String databaseType, int databaseUsageCount) {}
}
