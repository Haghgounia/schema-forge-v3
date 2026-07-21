package com.behsazan.schemaforge.dialect;

public interface DatabaseDialect {

    DatabaseProduct product();

    String name();

    IdentifierRules identifierRules();

    DataTypeRules dataTypeRules();

    DdlSyntax ddlSyntax();
}
