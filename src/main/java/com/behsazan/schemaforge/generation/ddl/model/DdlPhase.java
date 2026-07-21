package com.behsazan.schemaforge.generation.ddl.model;

public enum DdlPhase {
    PREAMBLE(0),
    SEQUENCES(100),
    TABLES(200),
    PRIMARY_KEYS(300),
    UNIQUE_KEYS(400),
    CHECK_CONSTRAINTS(500),
    FOREIGN_KEYS(600),
    INDEXES(700),
    VIEWS(800),
    MATERIALIZED_VIEWS(900),
    SYNONYMS(1000),
    TRIGGERS(1100),
    COMMENTS(1200),
    GRANTS(1300),
    POSTAMBLE(1400);

    private final int order;

    DdlPhase(int order) {
        this.order = order;
    }

    public int order() {
        return order;
    }
}
