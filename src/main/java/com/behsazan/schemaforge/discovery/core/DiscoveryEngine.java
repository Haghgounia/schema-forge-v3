package com.behsazan.schemaforge.discovery.core;

import com.behsazan.schemaforge.database.spi.DatabaseMetadataProvider;
import com.behsazan.schemaforge.discovery.domain.DiscoveryIssue;
import com.behsazan.schemaforge.discovery.domain.DiscoveryResult;
import com.behsazan.schemaforge.discovery.snapshot.DiscoverySnapshotBuilder;
import com.behsazan.schemaforge.domain.model.DatabaseSchema;
import com.behsazan.schemaforge.specification.domain.TableDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnBean(DatabaseMetadataProvider.class)
public class DiscoveryEngine {

    private final DatabaseMetadataProvider metadataProvider;
    private final List<DiscoveryRule> rules;
    private final DiscoverySnapshotBuilder snapshotBuilder;

    @Autowired
    public DiscoveryEngine(
            DatabaseMetadataProvider metadataProvider,
            List<DiscoveryRule> rules,
            DiscoverySnapshotBuilder snapshotBuilder) {
        this.metadataProvider = Objects.requireNonNull(metadataProvider, "metadataProvider must not be null");
        this.rules = List.copyOf(Objects.requireNonNull(rules, "rules must not be null"));
        this.snapshotBuilder = Objects.requireNonNull(snapshotBuilder, "snapshotBuilder must not be null");
    }

    public DiscoveryEngine(DatabaseMetadataProvider metadataProvider, List<DiscoveryRule> rules) {
        this(metadataProvider, rules, new DiscoverySnapshotBuilder());
    }

    public DiscoveryResult discover(TableDefinition tableDefinition) {
        Objects.requireNonNull(tableDefinition, "tableDefinition must not be null");
        String schemaName = tableDefinition.schema();
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("tableDefinition.schema must not be blank");
        }

        DatabaseSchema databaseSchema = metadataProvider.inspectSchema(schemaName);
        DiscoveryContext context = new DiscoveryContext(snapshotBuilder.build(tableDefinition, databaseSchema));
        List<DiscoveryIssue> issues = new ArrayList<>();
        for (DiscoveryRule rule : rules) {
            issues.addAll(rule.evaluate(context));
        }
        return new DiscoveryResult(issues);
    }
}
