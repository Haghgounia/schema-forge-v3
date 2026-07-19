package com.behsazan.schemaforge.database.domain;

public record SequenceState(
        String name,
        long minValue,
        long maxValue,
        long incrementBy,
        boolean cycle,
        int cacheSize,
        long lastNumber) {
}
