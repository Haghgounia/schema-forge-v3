package com.behsazan.schemaforge.database.spi;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.generation.spi.DatabaseType;

public interface DatabaseMetadataRepository {
    DatabaseType databaseType();
    DatabaseSchema inspectSchema(String schemaName);
}
