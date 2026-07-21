package com.behsazan.schemaforge.generation.ddl.model;

import java.util.List;
import java.util.Objects;

public record DdlStatement(
        DdlStatementType type,
        DdlObjectReference target,
        StatementOrder order,
        List<SqlFragment> fragments) {

    public DdlStatement {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(order, "order must not be null");
        fragments = fragments == null ? List.of() : List.copyOf(fragments);
        if (fragments.isEmpty()) {
            throw new IllegalArgumentException("fragments must not be empty");
        }
    }

    public static DdlStatement of(
            DdlStatementType type,
            DdlObjectReference target,
            StatementOrder order,
            SqlFragment fragment) {
        return new DdlStatement(type, target, order, List.of(fragment));
    }
}
