# Excel Comparison - Phase 1

## Routing rule

1. Parse one DOCX into the canonical schema.
2. Query the configured database for the documented table.
3. When the table is absent, generate only the CREATE SQL file.
4. When the table exists, generate only a comparison XLSX file.

## Current database adapter

Oracle is implemented through `OracleTableMetadataLookup`. The comparison writer and routing contract are DBMS-neutral. PostgreSQL can implement the same `DatabaseTableLookup` contract.

## Compared metadata

- added and removed columns
- possible column rename (reported as a candidate, never applied automatically)
- datatype, length, precision and scale
- required / nullable
- default expression
- comments
- primary key and unique membership
- foreign key membership and target
- index name, uniqueness, column composition, order and sort direction
- check constraints / document range-derived checks

## Output names

- New table: `TABLE-yyyyMMdd-HHmmss.sql`
- Existing table: `TABLE_compare_yyyyMMdd_HHmmss.xlsx`
