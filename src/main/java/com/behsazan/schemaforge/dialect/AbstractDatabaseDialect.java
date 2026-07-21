package com.behsazan.schemaforge.dialect;

import java.util.Objects;

public abstract class AbstractDatabaseDialect implements DatabaseDialect {

    private final IdentifierRules identifierRules;
    private final DataTypeRules dataTypeRules;
    private final DdlSyntax ddlSyntax;
    private final DatabaseCapabilities capabilities;
    private final IdentifierPolicy identifierPolicy;
    private final ReservedWordProvider reservedWordProvider;
    private final NamingStrategy namingStrategy;
    private final SqlTypeMapper sqlTypeMapper;

    protected AbstractDatabaseDialect(
            IdentifierRules identifierRules,
            DataTypeRules dataTypeRules,
            DdlSyntax ddlSyntax,
            DatabaseCapabilities capabilities,
            IdentifierPolicy identifierPolicy,
            ReservedWordProvider reservedWordProvider,
            NamingStrategy namingStrategy,
            SqlTypeMapper sqlTypeMapper) {
        this.identifierRules = Objects.requireNonNull(identifierRules, "identifierRules must not be null");
        this.dataTypeRules = Objects.requireNonNull(dataTypeRules, "dataTypeRules must not be null");
        this.ddlSyntax = Objects.requireNonNull(ddlSyntax, "ddlSyntax must not be null");
        this.capabilities = Objects.requireNonNull(capabilities, "capabilities must not be null");
        this.identifierPolicy = Objects.requireNonNull(identifierPolicy, "identifierPolicy must not be null");
        this.reservedWordProvider = Objects.requireNonNull(reservedWordProvider, "reservedWordProvider must not be null");
        this.namingStrategy = Objects.requireNonNull(namingStrategy, "namingStrategy must not be null");
        this.sqlTypeMapper = Objects.requireNonNull(sqlTypeMapper, "sqlTypeMapper must not be null");
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

    @Override
    public final DatabaseCapabilities capabilities() {
        return capabilities;
    }

    @Override
    public final IdentifierPolicy identifierPolicy() {
        return identifierPolicy;
    }

    @Override
    public final ReservedWordProvider reservedWordProvider() {
        return reservedWordProvider;
    }

    @Override
    public final NamingStrategy namingStrategy() {
        return namingStrategy;
    }

    @Override
    public final SqlTypeMapper sqlTypeMapper() {
        return sqlTypeMapper;
    }
}
