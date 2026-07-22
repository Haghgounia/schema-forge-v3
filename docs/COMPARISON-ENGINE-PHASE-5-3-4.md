# Phase 5.3.4 - Index Comparison

This phase completes comparison coverage for all table-level structures currently represented by the canonical `Table` model: columns, primary keys, unique keys, foreign keys, check constraints, and indexes.

## Index equality

Index names are not part of structural equality. An index is identified by:

1. index type;
2. ordered index columns;
3. sort direction of each column.

An exact structural match is treated as equal even when the document and database index names differ.

When an index with the same normalized name exists but has a different structure, the engine emits `INDEX_CHANGED`. Otherwise unmatched document and database indexes produce `INDEX_MISSING` and `INDEX_EXTRA` respectively.

## Oracle VARCHAR correction

The Oracle DDL policy now maps canonical `VARCHAR` to Oracle `VARCHAR2` before validating supported Oracle data types. This fixes the artifact-generation regression reported by `ArtifactGenerationServiceTest`.
