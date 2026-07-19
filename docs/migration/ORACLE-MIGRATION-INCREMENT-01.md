# Oracle Migration - Increment 01

This increment brings the first operational Oracle path from v2 into the v3 architecture without changing the project structure.

## Implemented path

`Oracle data dictionary -> JdbcOracleMetadataRepository -> OracleCanonicalSchemaMapper -> DatabaseSchema`

## Metadata covered

- Schema existence
- Tablespace existence
- Table existence and schema table listing
- Table and column comments
- Columns and Oracle data type details
- Primary, unique, foreign-key and check constraints
- Index columns, uniqueness and sort direction
- Oracle reserved words
- Cross-schema column usage and datatype usage statistics

## Runtime behavior

The Oracle JDBC repository is created only when a `NamedParameterJdbcTemplate` exists. Therefore SchemaForge can still start in offline generation mode without Oracle connection properties.

## Next increment

The next increment will expose database inspection through an API and migrate comparison/decision behavior used by the Oracle DDL generator.
