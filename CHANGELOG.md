
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

## 2026-07-21 - Phase 3.4 Core Database Independence

- Added `DatabaseDdlPlugin` and `DatabaseDdlPluginRegistry` as the single DDL plugin entry point.
- Migrated `DdlGenerationEngine` to resolve one complete plugin instead of three independent registries.
- Added database-owned `DdlGenerationPolicy` implementations for Oracle, PostgreSQL, and Standard SQL.
- Removed Oracle/PostgreSQL branching from foreign-key, index, sequence, physical-options, and data-type core generators.
- Deprecated the legacy multi-registry engine constructor and the individual dialect, renderer, and column-generator registries for removal in Phase 3.6.
- Retained the `yyyyMMdd-HHmmss` Gregorian 24-hour timestamp naming convention for generated SQL, Excel, and ZIP artifacts.

## Phase 3.5 - Audit columns and timestamped output names

- Added a vendor-neutral schema enrichment step before DDL generation.
- Added missing `CREATED_BY`, `CREATED_DATE`, `LAST_MODIFIED_BY`, and `LAST_MODIFIED_DATE` columns to the end of every table.
- Existing audit columns are preserved and are not duplicated.
- Standardized generated script names on Gregorian `yyyyMMdd-HHmmss` with 24-hour `HH`.

## Phase 3.6 - Robust Parser & Recovery Engine (start)

### Test isolation fix
- Database metadata integrations now require both `enabled=true` and a non-blank JDBC URL.
- Batch DOCX DDL generation can enable Oracle/PostgreSQL output without forcing live database connections.
- Prevents `schemaforge.oracle.url is required` and PostgreSQL equivalent during offline DDL regression tests.

### Next implementation increment
- Datatype normalization and identifier recovery will be added after the batch regression test is unblocked.

## Phase 3.6 - LanguageTool spelling validation restoration

- Restored the Phase 2 LanguageTool integration in the V3 validation architecture.
- Added fail-open HTTP integration with connect/request timeouts.
- Added bounded replacement suggestions and technical-term filtering.
- Added a 24-hour in-memory result cache to avoid repeated public API calls.
- Added `ColumnNameSpellingRule`; findings are warnings and never modify generated identifiers.
- Spell checking remains disabled by default and can be enabled with
  `SCHEMAFORGE_SPELL_CHECK_ENABLED=true`.

## 2026-07-22 - PostgreSQL identity/default conflict fix

- PostgreSQL column generation now suppresses any default expression when the column is marked as identity.
- Prevents invalid output such as `GENERATED BY DEFAULT AS IDENTITY DEFAULT BIM.SEQ_CITIES.NEXTVAL`.
- Non-identity defaults remain unchanged.
- Added focused regression tests for both identity and non-identity columns.

## 2026-07-22 - Numeric Range normalization
- Added a database-independent canonical numeric range model and parser.
- Persian/Arabic digits are normalized to ASCII digits.
- Persian and English descriptions in DOCX Range cells are ignored while numeric intervals/enumerations are retained.
- Range values now generate portable CHECK expressions such as `BETWEEN` and `IN`.
- Added unit tests and an end-to-end fixture for ACCOUNTING_CONFIGURATIONS.

## 2026-07-22 - Conditional SQL / comparison Excel routing

- Added DBMS-neutral `DatabaseTableLookup`.
- Added Oracle single-table metadata lookup without loading the entire schema.
- Changed artifact routing:
  - table absent or database lookup disabled: SQL only;
  - table present: comparison Excel only.
- Added canonical table-to-table comparison workbook with the same 22-column layout as the supplied samples.
- Comparison covers added/removed columns, possible rename, datatype, nullability, default, comments, PK, FK, unique, index membership/order, and check constraints.

## 2026-07-22 - Excel Compare Phase 5.1
- Replaced the Oracle-selected table lookup with the DBMS-neutral `DatabaseMetadataReader` port.
- Added `DatabaseMetadataReaderRegistry` keyed by `DatabaseProduct`.
- Added configuration-based target product resolution from `schemaforge.generation.default-database`.
- Removed Oracle hard-coding from `ArtifactGenerationService` for both metadata lookup and DDL generation.
- Added registry unit tests and architecture notes.

## 2026-07-22 - Table Comparison Excel integration

- Made `SchemaComparisonEngine` the single source of truth for the canonical table comparison workbook.
- Added `TABLE_SUMMARY`, table-detail, and `DIFFERENCES` sheets.
- Added atomic difference rows with scope, property, expected/actual values, severity, resolution strategy, and message.
- Preserved the existing side-by-side document/database column view and column usage counts.
- Kept possible-column-rename presentation in the detail sheet while structural differences come from the comparison engine.
- Added a regression test for the three-sheet workbook structure.
- Confirmed Oracle canonical `VARCHAR` rendering is mapped to `VARCHAR2` by `OracleDdlGenerationPolicy` and covered by `OracleDialectPolicyTest`.

## 2026-07-22 - Sprint 1 Table Comparison Excel UX

- Added professional TABLE_SUMMARY dashboard title and severity legend.
- Added stable CMP difference codes to the DIFFERENCES sheet.
- Added rule-based recommendations without changing comparison-engine APIs.
- Added severity-based cell coloring for Critical, High, Medium, Low, and Info.
- Added document hyperlinks from summary to table details and differences.
- Retained filters, freeze panes, automatic column sizing, and atomic difference rows.
- Expanded Excel writer tests for codes, recommendations, hyperlinks, and filters.
