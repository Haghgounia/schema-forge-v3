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
