package com.behsazan.schemaforge.generation.ddl.model;

public enum DdlStatementType {
    CREATE_SEQUENCE,
    CREATE_TABLE,
    CREATE_PRIMARY_KEY,
    CREATE_UNIQUE_KEY,
    CREATE_CHECK_CONSTRAINT,
    CREATE_FOREIGN_KEY,
    CREATE_INDEX,
    CREATE_VIEW,
    CREATE_MATERIALIZED_VIEW,
    CREATE_SYNONYM,
    CREATE_TRIGGER,
    COMMENT,
    GRANT,
    CUSTOM
}
