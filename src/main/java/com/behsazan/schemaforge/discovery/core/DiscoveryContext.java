package com.behsazan.schemaforge.discovery.core;

import com.behsazan.schemaforge.discovery.snapshot.DiscoverySnapshot;
import com.behsazan.schemaforge.discovery.snapshot.DiscoverySnapshotBuilder;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.Objects;

public record DiscoveryContext(DiscoverySnapshot snapshot) {

    public DiscoveryContext {
        Objects.requireNonNull(snapshot, "snapshot must not be null");
    }

    public DiscoveryContext(TableDefinition documentTable, DatabaseSchema databaseSchema) {
        this(new DiscoverySnapshotBuilder().build(documentTable, databaseSchema));
    }

    public TableDefinition documentTable() {
        return snapshot.documentTable();
    }

    public DatabaseSchema databaseSchema() {
        return snapshot.databaseSchema();
    }
}
