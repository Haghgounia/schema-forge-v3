package com.behsazan.schemaforge.database.domain;

public record ConstraintState(
        String name,
        String type,
        String columnName,
        Integer columnPosition,
        String expression,
        String referencedOwner,
        String referencedTable,
        String referencedColumn,
        String deleteRule) {
}
