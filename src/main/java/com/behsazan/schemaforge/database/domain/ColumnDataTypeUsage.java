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
            case "NUMBER" -> numberSignature();
            case "VARCHAR2", "VARCHAR", "CHAR", "NVARCHAR2", "NCHAR" -> characterSignature(normalizedType);
            default -> normalizedType;
        };
    }

    private String numberSignature() {
        if (dataPrecision == null) return "NUMBER";
        if (dataScale == null || dataScale == 0) return "NUMBER(" + dataPrecision + ")";
        return "NUMBER(" + dataPrecision + "," + dataScale + ")";
    }

    private String characterSignature(String normalizedType) {
        return dataLength == null ? normalizedType : normalizedType + "(" + dataLength + ")";
    }
}
