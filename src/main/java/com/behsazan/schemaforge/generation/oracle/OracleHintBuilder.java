package com.behsazan.schemaforge.generation.oracle;

import com.behsazan.schemaforge.database.domain.ColumnDataTypeUsage;
import com.behsazan.schemaforge.database.oracle.OracleDictionaryCache;
import com.behsazan.schemaforge.specification.domain.ColumnDefinition;
import com.behsazan.schemaforge.specification.domain.DataTypeDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class OracleHintBuilder {

    private final OracleDictionaryCache dictionaryCache;

    public OracleHintBuilder() {
        this(null);
    }

    public OracleHintBuilder(OracleDictionaryCache dictionaryCache) {
        this.dictionaryCache = dictionaryCache;
    }

    public String build(TableDefinition table) {
        Objects.requireNonNull(table, "table must not be null");

        StringBuilder sql = new StringBuilder();
        sql.append("""

                -- =====================================================
                -- SCHEMAFORGE DBA HINTS
                -- =====================================================

                """);

        for (ColumnDefinition column : table.columns()) {
            appendReservedWordHint(sql, column);
            appendColumnUsageHint(sql, column);
            appendDataTypeConsistencyHint(sql, column);
        }

        return sql.toString();
    }

    private void appendReservedWordHint(StringBuilder sql, ColumnDefinition column) {
        if (dictionaryCache == null || !dictionaryCache.isReservedWord(column.name())) {
            return;
        }

        sql.append("""
                -- ERROR: Column %s is an Oracle reserved word.

                """.formatted(column.name()));
    }

    private void appendColumnUsageHint(StringBuilder sql, ColumnDefinition column) {
        if (dictionaryCache == null) {
            return;
        }

        int usageCount = dictionaryCache.getColumnUsageCount(column.name());
        if (usageCount <= 0) {
            return;
        }

        sql.append("""
                -- INFO: Column %s is used in %d database table(s).

                """.formatted(column.name(), usageCount));
    }

    private void appendDataTypeConsistencyHint(StringBuilder sql, ColumnDefinition column) {
        if (dictionaryCache == null) {
            return;
        }

        List<ColumnDataTypeUsage> usages = dictionaryCache.getColumnDataTypeUsages(column.name());
        if (usages.isEmpty()) {
            return;
        }

        String documentType = typeSignature(column.dataType());
        String standardType = findStandardType(usages);
        if (standardType.isBlank() || standardType.equals(documentType)) {
            return;
        }

        int matchingUsageCount = usages.stream()
                .filter(usage -> standardType.equals(usage.typeSignature()))
                .mapToInt(ColumnDataTypeUsage::usageCount)
                .sum();

        sql.append("""
                -- WARNING: Column %s uses %s in the document.
                -- Existing standard type is %s across %d database table(s).

                """.formatted(column.name(), documentType, standardType, matchingUsageCount));
    }

    private String findStandardType(List<ColumnDataTypeUsage> usages) {
        return usages.stream()
                .collect(Collectors.toMap(
                        ColumnDataTypeUsage::typeSignature,
                        ColumnDataTypeUsage::usageCount,
                        Integer::sum))
                .entrySet()
                .stream()
                .max(Comparator
                        .comparingInt((Map.Entry<String, Integer> entry) -> entry.getValue())
                        .thenComparing(Map.Entry::getKey, Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private String typeSignature(DataTypeDefinition dataType) {
        String normalizedName = dataType.name().trim().toUpperCase(Locale.ROOT);
        return switch (normalizedName) {
            case "NUMBER", "DECIMAL", "NUMERIC" -> numericSignature(normalizedName, dataType);
            case "VARCHAR2", "VARCHAR", "CHAR", "NVARCHAR2", "NCHAR" ->
                    characterSignature(normalizedName, dataType);
            default -> normalizedName;
        };
    }

    private String numericSignature(String name, DataTypeDefinition dataType) {
        if (dataType.precision() == null) {
            return name;
        }
        if (dataType.scale() == null || dataType.scale() == 0) {
            return name + "(" + dataType.precision() + ")";
        }
        return name + "(" + dataType.precision() + "," + dataType.scale() + ")";
    }

    private String characterSignature(String name, DataTypeDefinition dataType) {
        Integer length = dataType.length();
        return length == null ? name : name + "(" + length + ")";
    }
}
