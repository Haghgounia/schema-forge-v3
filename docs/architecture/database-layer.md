# Database metadata layer

The database layer is vendor-neutral at its core:

- `DatabaseDictionaryProvider`: reserved words, column usage counts, and datatype usage statistics.
- `DatabaseMetadataProvider`: full live schema inspection in addition to dictionary metadata.
- `DatabaseDictionaryCache`: stores one immutable dictionary snapshot per `DatabaseType`.
- Vendor adapters live below `database/<vendor>` and own all vendor SQL.

Implemented adapters:

- Oracle: `JdbcOracleMetadataProvider`
- PostgreSQL: `JdbcPostgreSqlMetadataProvider`

A connection failure is isolated per metadata query. The cache falls back to empty reserved words,
zero usage counts, and empty datatype usages, so DDL generation continues.

## PostgreSQL configuration

```yaml
schemaforge:
  postgresql:
    enabled: true
    url: jdbc:postgresql://localhost:5432/appdb
    username: app_user
    password: secret
    query-timeout-seconds: 60
```

Environment variables use the `SCHEMAFORGE_POSTGRESQL_*` prefix.

To add another DBMS, implement `DatabaseMetadataProvider`, return its `DatabaseType`, and provide a
qualified `NamedParameterJdbcTemplate`. No change to `DatabaseDictionaryCache` is required.
