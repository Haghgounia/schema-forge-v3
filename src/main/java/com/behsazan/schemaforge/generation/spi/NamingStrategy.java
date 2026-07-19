package com.behsazan.schemaforge.generation.spi;

import com.behsazan.schemaforge.specification.domain.IndexDefinition;
import com.behsazan.schemaforge.specification.domain.TableDefinition;

public interface NamingStrategy {
    String primaryKeyName(TableDefinition table);
    String foreignKeyName(TableDefinition child, TableDefinition parent);
    String indexName(TableDefinition table, IndexDefinition index);
}
