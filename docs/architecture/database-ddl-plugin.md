# Database DDL Plugin Architecture

The DDL core resolves one `DatabaseDdlPlugin` for the requested `DatabaseProduct`.
A plugin supplies its dialect, renderer, and column-definition generator. Vendor-specific
DDL decisions are delegated through `DdlGenerationPolicy`; core generators do not branch
on Oracle or PostgreSQL.

## Extension contract

A new database implementation provides:

1. `DatabaseDialect`, including a `DdlGenerationPolicy`.
2. `DdlRenderer`.
3. `ColumnDefinitionGenerator`.
4. `DatabaseDdlPlugin` registration.

The `DdlGenerationEngine` and application services require no database-specific changes.
