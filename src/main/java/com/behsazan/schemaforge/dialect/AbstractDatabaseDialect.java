package com.behsazan.schemaforge.dialect;

import java.util.Objects;

public abstract class AbstractDatabaseDialect implements DatabaseDialect {

    private final IdentifierRules identifierRules;
    private final DataTypeRules dataTypeRules;
    private final DdlSyntax ddlSyntax;

    protected AbstractDatabaseDialect(
            IdentifierRules identifierRules,
            DataTypeRules dataTypeRules,
            DdlSyntax ddlSyntax) {
        this.identifierRules = Objects.requireNonNull(identifierRules, "identifierRules must not be null");
        this.dataTypeRules = Objects.requireNonNull(dataTypeRules, "dataTypeRules must not be null");
        this.ddlSyntax = Objects.requireNonNull(ddlSyntax, "ddlSyntax must not be null");
    }

    @Override
    public final IdentifierRules identifierRules() {
        return identifierRules;
    }

    @Override
    public final DataTypeRules dataTypeRules() {
        return dataTypeRules;
    }

    @Override
    public final DdlSyntax ddlSyntax() {
        return ddlSyntax;
    }
}
