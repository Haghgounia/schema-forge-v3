package com.behsazan.schemaforge.generation.spi;

import java.util.Set;

public interface DatabaseDialect {
    DatabaseType type();
    Set<DatabaseCapability> capabilities();
    TypeMapper typeMapper();
    IdentifierRules identifierRules();
    NamingStrategy namingStrategy();
    GenerationValidator validator();
    DdlGenerator ddlGenerator();

    default boolean supports(DatabaseCapability capability) {
        return capabilities().contains(capability);
    }
}
