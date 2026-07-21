
## 2026-07-21 - Phase 3.3 plugin architecture and deprecation pass

- Made `ColumnDefinitionGenerator` a self-describing database plugin via `product()`.
- Replaced hard-coded column generator registrations with Spring list discovery.
- Unified system status reporting on the active `com.behsazan.schemaforge.dialect.DialectRegistry`.
- Added registry introspection for registered dialects, renderers, and column generators.
- Marked the retired Oracle generation pipeline and legacy generation SPI/registry with
  `@Deprecated(forRemoval = true, since = "3.3")`; removal remains scheduled for Phase 3.6.
- Kept deprecated classes binary/source compatible so regression tests and downstream callers can migrate safely.

## Index Engine v1

- Added standalone `CREATE INDEX`, `CREATE UNIQUE INDEX`, and Oracle `CREATE BITMAP INDEX` generation.
- Added schema-qualified index names and table names.
- Added composite index columns with explicit `ASC`/`DESC` ordering.
- Integrated indexes into `TableScriptGenerator` under `DdlPhase.INDEXES`.
- Added unit coverage for Oracle index generation and unsupported index types.
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

## Increment 03.10 - Priority-One Discovery Engine

- Added the DBMS-neutral discovery engine and rule contract.
- Added field-usage discovery for every document column.
- Added advisory data-type consistency warnings based on the most frequent existing canonical type.
- Added structured discovery issues, categories, severities and result aggregation.
- Added unit tests for field usage, type consistency and engine orchestration.

## Recovery Patch 03 - DOCX header aliases

- Added Persian and English aliases for metadata and column-table headers.
- Added a regression test that builds and parses a DOCX with Persian headers.
- No package refactoring or architecture changes.

## 2026-07-20 - Phase 3 DOCX corpus scan

- Added `DocxCorpusBatchTest` for recursive batch processing of large DOCX collections.
- Added CSV reports for per-file results and unsupported data type expressions.
- Added a summary report and SQL output for successfully processed documents.
- Added support for `CHAR` and `BYTE` length semantics in DOCX data type expressions.

## 2026-07-21 - DDL Completion Pack v1
- Added table and column COMMENT generation with SQL literal escaping.
- Added CREATE SEQUENCE generation with start, increment, bounds, cycle and cache options.
- Added object GRANT generation, including WITH GRANT OPTION.
- Added Oracle physical table options: TABLESPACE, PCTFREE, INITRANS, MAXTRANS, LOGGING and COMPRESS.
- Added SchemaScriptGenerator to compose existing generators without introducing the proposed larger deployment architecture.
- Added six tests; expected baseline is 116 tests with 0 failures and 0 errors.

## 2026-07-21 - PostgreSQL DDL Engine v1
- Added PostgreSQL as a first-class DatabaseProduct and dialect.
- Added PostgreSQL identifier, reserved-word, naming, capability, DDL syntax and SQL type policies.
- Added Oracle-to-PostgreSQL canonical type aliases for VARCHAR2, NUMBER, CLOB, BLOB, RAW and related types.
- Added PostgreSQL column generation with identity, defaults and nullability.
- Added PostgreSQL DDL rendering with optional psql ON_ERROR_STOP preamble.
- Added PostgreSQL-specific sequence syntax and index naming behavior.
- Added PostgreSQL TABLESPACE validation for physical table options.
- Added four PostgreSQL regression tests; expected baseline is 120 tests with 0 failures and 0 errors.

## 2026-07-21 Oracle dictionary validation wiring

- Wired Oracle column usage counts into generated column definitions.
- Added Oracle reserved-word error comments for document columns.
- Added datatype mismatch warnings against the dominant existing Oracle datatype.
- New column names are accepted without datatype warnings.
- Oracle dictionary failures now fall back to zero/empty metadata without stopping generation.
- Removed duplicate Spring registration of the deprecated JdbcOracleMetadataRepository adapter.

## 2026-07-21 - Phase 3.2 DDL Engine Extraction

- Added vendor-neutral `DdlGenerationEngine`, request, and result types.
- Added registry-based selection of vendor-specific column definition generators.
- Added Spring composition for Oracle and PostgreSQL dialects and renderers.
- Migrated `ArtifactGenerationService` away from `generation.oracle.OracleDdlGenerator`.
- Preserved Oracle as the current default output target while removing the application-layer Oracle generator dependency.
