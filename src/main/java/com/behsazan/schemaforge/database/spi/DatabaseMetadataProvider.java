package com.behsazan.schemaforge.database.spi;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;

/**
 * DBMS-neutral service-provider interface for reading live database metadata.
 * Implementations are vendor adapters such as Oracle, DB2 for z/OS, and
 * PostgreSQL.
 */
public interface DatabaseMetadataProvider extends DatabaseDictionaryProvider {

    DatabaseSchema inspectSchema(String schemaName);
}
