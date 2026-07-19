package com.behsazan.schemaforge.database.domain;

public record IndexState(
        String name,
        boolean unique,
        String columnName,
        Integer columnPosition,
        String descend) {
}
