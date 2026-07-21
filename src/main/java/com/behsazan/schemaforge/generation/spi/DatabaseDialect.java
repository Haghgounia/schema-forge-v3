package com.behsazan.schemaforge.generation.spi;

import java.util.Set;

/**
 * @deprecated since 3.3. Replaced by {@link com.behsazan.schemaforge.dialect.DatabaseDialect}.
 * Scheduled for removal in Phase 3.6.
 */
@Deprecated(forRemoval = true, since = "3.3")
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
