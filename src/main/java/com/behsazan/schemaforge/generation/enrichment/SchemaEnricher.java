package com.behsazan.schemaforge.generation.enrichment;

import com.behsazan.schemaforge.domain.model.DatabaseSchema;

/** Enriches a canonical schema before DDL generation. */
@FunctionalInterface
public interface SchemaEnricher {
    DatabaseSchema enrich(DatabaseSchema schema);
}
