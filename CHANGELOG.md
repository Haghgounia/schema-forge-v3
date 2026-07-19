# Changelog

## 3.0.0-SNAPSHOT - Backend Foundation Increment 01

- Added typed application configuration.
- Added parser registry and parse application service.
- Added standard API success/error contracts.
- Added correlation ID filter.
- Added UI CORS boundary.
- Made backend startup independent from Oracle availability.
- Added status API and foundation tests.

## Foundation Sprint 02
- Added the canonical DBMS-neutral domain model.
- Added validated value objects and aggregate builders.
- Migrated parser and generation contracts to `DatabaseSchema`.
- Added canonical model unit tests and release notes.

## 3.0.0-SNAPSHOT - Oracle migration increment 01

- Migrated Oracle metadata access contracts into the v3 provider architecture.
- Added Oracle schema, table, column, constraint, index and dictionary metadata queries.
- Added mapping from Oracle metadata to the canonical `DatabaseSchema` model.
- Migrated database inspection result and service to the canonical model.
- Kept Oracle connectivity optional so the application can start without a configured datasource.
- Added unit tests for canonical Oracle mapping and provider selection.

## 3.0.0-SNAPSHOT - Oracle Migration Increment 02

- Added opt-in Oracle connection configuration through environment variables.
- Added database inspection REST endpoints for full canonical schema and summary output.
- Extended Oracle discovery to sequences, views, materialized views, synonyms, table triggers, and standalone procedures/functions.
- Preserved the existing single-module Maven and package structure.

## Oracle Migration Increment 03.1
- Added canonical SQL document model.
- Added Oracle table DDL generation and SQL renderer.
- Added install.sql artifact generation.
- Added end-to-end Oracle DDL generation test.

## Increment 03.1 Revision 1 - Oracle legacy sequence-default alignment

- Added Oracle sequence generation before table generation.
- Aligned generated sequence syntax with the supplied legacy golden scripts.
- Uses `DEFAULT <SCHEMA>.<SEQUENCE>.NEXTVAL` through the canonical column default expression.
- Removed identity generation from the Oracle end-to-end reference test.
- Added ordering assertion to guarantee `CREATE SEQUENCE` precedes `CREATE TABLE`.

## 2026-07-19 - Increment 03.1 compile repair
- Restored missing SQL intermediate model classes.
- Restored Oracle table generator, SQL renderer, and canonical type mapper.
- No project structure changes.

## 3.0.0-SNAPSHOT - Oracle DDL Increment 03.1

- Added Oracle constraint generation for primary keys, unique keys, check constraints, and foreign keys.
- Added Oracle standalone index generation.
- Preserved dependency order: sequences, tables, local constraints, foreign keys, indexes, comments.
- Disabled Oracle identity clause generation to retain the V2 sequence plus `DEFAULT ...NEXTVAL` convention.
- Added regression coverage for Oracle constraint and index ordering.
- No package refactoring or structural architecture changes.

## Increment 03.3 - Oracle views and synonyms

- Added Oracle generation for standard views.
- Added Oracle generation for materialized views using the existing canonical `View.materialized` flag.
- Added Oracle generation for private and public synonyms.
- Added view and materialized-view comments when comment generation is enabled.
- Added dependency ordering: tables, constraints, indexes, views, materialized views, synonyms, comments.
- Added regression coverage for statement generation, query terminator normalization, and dependency ordering.

## Increment 03.4 - DOCX specification parser migration

- Migrated the table-design DOCX parser into the v3 canonical-model pipeline.
- Added extraction of table metadata, columns, data types, required/default attributes, PK, FK, unique groups, index groups and checks.
- Added legacy Oracle identity conversion to sequence plus `NEXTVAL` default.
- Registered the DOCX parser as a Spring component.
- Added a regression test using the CITIES table-design document.

## Increment 03.5 - DOCX real-document regression corpus

- Added regression coverage for six real table-design DOCX documents.
- Covered CITIES, PROVINCES, PROVINCE_HISTORY, DEPOSITS, CONTRACTS and PROPOSEDS.
- Added a parameterized parser corpus test that verifies schema, table and column extraction for every document.
- Corrected the CITIES default-value assertion to unwrap the canonical column before accessing its value.
- No package refactoring or architecture changes.

## Increment 3.7 - Schema comparison Excel migration

- Added DBMS-independent SchemaCompareExcelWriter based on the canonical database model.
- Added column usage counts and document/database difference detection.
- Added regression test for workbook generation and difference markers.

## Increment 03.8 - Oracle Hints
- Migrated OracleDictionaryCache.
- Added OracleHintBuilder with reserved-word, field-usage, and data-type consistency hints.
- Added unit tests for cache normalization and hint generation.

## Increment 03.9 - Database Metadata Provider SPI

- Added the DBMS-neutral `DatabaseMetadataProvider` SPI.
- Added the Oracle-specific `OracleMetadataProvider` adapter contract.
- Renamed the JDBC implementation to `JdbcOracleMetadataProvider`.
- Migrated application and Oracle cache consumers from repository contracts to provider contracts.
- Retained deprecated repository interfaces as source-compatible migration bridges.
- Added SPI and architecture regression tests.
