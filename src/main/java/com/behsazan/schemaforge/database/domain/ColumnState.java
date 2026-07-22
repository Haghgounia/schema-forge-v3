package com.behsazan.schemaforge.database.domain;

public record ColumnState(
        Integer columnId,
        String name,
        String rawDataType,
        Integer length,
        Integer charLength,
        String charUsed,
        Integer precision,
        Integer scale,
        boolean nullable,
        String defaultValue,
        String comment,
        boolean identity) {
}
