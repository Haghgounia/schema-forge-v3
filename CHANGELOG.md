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
