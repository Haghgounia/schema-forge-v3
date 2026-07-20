package com.behsazan.schemaforge.discovery.snapshot;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.domain.model.Table;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class DiscoverySnapshotBuilder {

    public DiscoverySnapshot build(TableDefinition documentTable, DatabaseSchema databaseSchema) {
        Objects.requireNonNull(documentTable, "documentTable must not be null");
        Objects.requireNonNull(databaseSchema, "databaseSchema must not be null");

        Map<String, List<ColumnUsage>> mutableIndex = new LinkedHashMap<>();
        for (Table table : databaseSchema.tables()) {
            table.columns().forEach(column -> mutableIndex
                    .computeIfAbsent(DiscoverySnapshot.normalize(column.name().value()), ignored -> new ArrayList<>())
                    .add(new ColumnUsage(table, column)));
        }

        Map<String, List<ColumnUsage>> immutableIndex = new LinkedHashMap<>();
        mutableIndex.forEach((name, usages) -> immutableIndex.put(name, List.copyOf(usages)));
        return new DiscoverySnapshot(documentTable, databaseSchema, immutableIndex);
    }
}
