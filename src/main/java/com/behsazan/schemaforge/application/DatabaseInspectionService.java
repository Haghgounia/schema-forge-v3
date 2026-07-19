package com.behsazan.schemaforge.application;

import com.behsazan.schemaforge.database.domain.DatabaseInspectionResult;
import com.behsazan.schemaforge.generation.spi.DatabaseType;

public interface DatabaseInspectionService {
    DatabaseInspectionResult inspect(DatabaseType databaseType, String schemaName);
}
