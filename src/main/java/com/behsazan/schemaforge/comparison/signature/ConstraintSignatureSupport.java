package com.behsazan.schemaforge.comparison.signature;

import com.behsazan.schemaforge.domain.valueobject.Identifier;
import java.util.List;
import java.util.stream.Collectors;

final class ConstraintSignatureSupport {
    private ConstraintSignatureSupport() { }

    static String orderedColumns(List<Identifier> columns) {
        return columns.stream().map(Identifier::normalized).collect(Collectors.joining("|"));
    }
}
