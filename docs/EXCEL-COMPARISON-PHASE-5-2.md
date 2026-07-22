# Phase 5.2 - Oracle metadata completeness

This phase extends the existing Phase 5.1 structure without package or service refactoring.

Added metadata fidelity:

- Oracle identity column (`ALL_TAB_COLUMNS.IDENTITY_COLUMN`)
- Foreign-key delete rule (`ALL_CONSTRAINTS.DELETE_RULE`)
- Canonical mapping of `CASCADE` and `SET NULL`
- Excel `DIFF` detection for identity changes
- Foreign-key comparison now includes delete/update actions

Existing SQL-or-Excel routing and DBMS-neutral `DatabaseMetadataReader` remain unchanged.
