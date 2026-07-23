# Oracle Tablespace Audit

## Scope
Focused completion only. No package refactor and no duplicate generator classes were introduced.

## Existing code reused
- `TableDdlGenerator`
- `PrimaryKeyGenerator`
- `UniqueKeyGenerator`
- `IndexGenerator`
- `ConstraintSqlSupport`

## Implemented Oracle conventions
- Table: `TABLESPACE TS_<SCHEMA>` when no explicit table tablespace exists.
- Primary key backing index: `TABLESPACE ITS_<SCHEMA>`.
- Unique-key backing index: `TABLESPACE ITS_<SCHEMA>`.
- Standalone index: `TABLESPACE ITS_<SCHEMA>`.

## Non-Oracle behavior
PostgreSQL and other dialect output paths are unchanged.

## Verification note
Compilation/tests must be run in the project environment. The isolated build environment could not download the Maven wrapper distribution.
