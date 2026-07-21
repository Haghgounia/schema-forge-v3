package com.behsazan.schemaforge.dialect;

public interface DatabaseDialect {

    DatabaseProduct product();

    String name();

    IdentifierRules identifierRules();

    DataTypeRules dataTypeRules();

    DdlSyntax ddlSyntax();

    DdlGenerationPolicy ddlGenerationPolicy();

    DatabaseCapabilities capabilities();

    IdentifierPolicy identifierPolicy();

    ReservedWordProvider reservedWordProvider();

    NamingStrategy namingStrategy();

    SqlTypeMapper sqlTypeMapper();

    default boolean supports(DatabaseCapability capability) {
        return capabilities().supports(capability);
    }
}
