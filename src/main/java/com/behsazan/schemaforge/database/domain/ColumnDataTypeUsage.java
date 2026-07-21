package com.behsazan.schemaforge.database.domain;

import java.util.Locale;

public record ColumnDataTypeUsage(
        String columnName,
        String dataType,
        Integer dataLength,
        Integer dataPrecision,
        Integer dataScale,
        int usageCount) {

    public String typeSignature() {
        String normalizedType = dataType == null ? "" : dataType.trim().toUpperCase(Locale.ROOT);
        return switch (normalizedType) {
            case "NUMBER", "NUMERIC", "DECIMAL" -> numericSignature(normalizedType);
            case "VARCHAR2", "VARCHAR", "CHAR", "NVARCHAR2", "NCHAR",
                    "CHARACTER VARYING", "CHARACTER", "BIT VARYING", "BIT" -> characterSignature(normalizedType);
            default -> normalizedType;
        };
    }

    private String numericSignature(String normalizedType) {
        if (dataPrecision == null) return normalizedType;
        if (dataScale == null || dataScale == 0) return normalizedType + "(" + dataPrecision + ")";
        return normalizedType + "(" + dataPrecision + "," + dataScale + ")";
    }

    private String characterSignature(String normalizedType) {
        return dataLength == null ? normalizedType : normalizedType + "(" + dataLength + ")";
    }
}
