package com.behsazan.schemaforge.database.spi;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.spi.DatabaseType;

/**
 * DBMS-neutral service provider interface for reading live database metadata.
 */
public interface DatabaseMetadataProvider {

    DatabaseType databaseType();

    DatabaseSchema inspectSchema(String schemaName);
}
