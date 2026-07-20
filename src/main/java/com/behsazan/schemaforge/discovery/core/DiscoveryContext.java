package com.behsazan.schemaforge.discovery.core;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.Objects;

public record DiscoveryContext(TableDefinition documentTable, DatabaseSchema databaseSchema) {

    public DiscoveryContext {
        Objects.requireNonNull(documentTable, "documentTable must not be null");
        Objects.requireNonNull(databaseSchema, "databaseSchema must not be null");
    }
}
