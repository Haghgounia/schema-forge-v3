package com.behsazan.schemaforge.generation.ddl.model;

import java.util.Objects;

public record StatementOrder(DdlPhase phase, int position) implements Comparable<StatementOrder> {
    public StatementOrder {
        Objects.requireNonNull(phase, "phase must not be null");
        if (position < 0) {
            throw new IllegalArgumentException("position must not be negative");
        }
    }

    public static StatementOrder first(DdlPhase phase) {
        return new StatementOrder(phase, 0);
    }

    @Override
    public int compareTo(StatementOrder other) {
        int phaseComparison = Integer.compare(phase.order(), other.phase.order());
        return phaseComparison != 0 ? phaseComparison : Integer.compare(position, other.position);
    }
}
