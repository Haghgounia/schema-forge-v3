package com.behsazan.schemaforge.database.oracle;

import com.behsazan.schemaforge.application.database.DatabaseMetadataReader;
import com.behsazan.schemaforge.dialect.DatabaseProduct;
import com.behsazan.schemaforge.domain.model.Table;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/** Reads a single Oracle table without loading the entire schema. */
@Component
@ConditionalOnBean(OracleMetadataProvider.class)
public final class OracleTableMetadataLookup implements DatabaseMetadataReader {
    private final OracleMetadataProvider provider;
    private final OracleCanonicalSchemaMapper mapper = new OracleCanonicalSchemaMapper();

    public OracleTableMetadataLookup(OracleMetadataProvider provider) {
        this.provider = provider;
    }

    @Override
    public DatabaseProduct databaseProduct() {
        return DatabaseProduct.ORACLE;
    }

    @Override
    public Optional<Table> readTable(String schemaName, String tableName) {
        if (!provider.tableExists(schemaName, tableName)) {
            return Optional.empty();
        }
        return Optional.of(mapper.mapTable(
                schemaName,
                tableName,
                provider.findTableComment(schemaName, tableName),
                provider.findColumns(schemaName, tableName),
                provider.findConstraints(schemaName, tableName),
                provider.findIndexes(schemaName, tableName)));
    }

    @Override
    public Map<String, Integer> columnUsageCounts() {
        return provider.loadColumnUsageCounts();
    }
}
