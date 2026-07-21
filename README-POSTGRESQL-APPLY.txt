SchemaForge PostgreSQL DDL Engine v1
====================================

Apply the patch at the project root, preserving paths, then run:

    mvn clean test

Expected result:

    Tests run: 120
    Failures: 0
    Errors: 0
    Skipped: 1
    BUILD SUCCESS

Main entry points:

    com.behsazan.schemaforge.dialect.postgresql.PostgreSqlDialect
    com.behsazan.schemaforge.generation.ddl.generator.table.postgresql.PostgreSqlColumnDefinitionGenerator
    com.behsazan.schemaforge.generation.ddl.renderer.postgresql.PostgreSqlDdlRenderer

Scope:
- CREATE TABLE and identity columns
- PK, UK, CHECK and FK constraints
- normal and unique indexes
- comments
- sequences
- grants
- PostgreSQL TABLESPACE clause
- Oracle canonical type aliases mapped to PostgreSQL SQL types

The Maven wrapper could not download Maven in the build container. Changed production sources were compiled directly with javac 21 successfully. Run the full Maven test suite in the project environment.
