package com.behsazan.schemaforge.database.domain;

public record TriggerState(
        String name,
        String tableOwner,
        String tableName,
        String timing,
        String event,
        String body) {
}
