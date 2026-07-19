package com.behsazan.schemaforge.generation.spi;

public enum DatabaseCapability {
    SEQUENCE,
    IDENTITY,
    TABLESPACE,
    PARTITIONING,
    COMMENT_ON,
    DEFERRABLE_CONSTRAINT,
    INCLUDE_INDEX_COLUMNS,
    GENERATED_COLUMNS,
    ROLLBACK_SCRIPT,
    DATABASE_METADATA_INSPECTION
}
