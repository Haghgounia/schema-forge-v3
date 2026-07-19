package com.behsazan.schemaforge.specification.domain;

public record SequenceDefinition(
        String name,
        Long startWith,
        Long incrementBy,
        Long minValue,
        Long maxValue,
        boolean cycle,
        Integer cacheSize) {
}
