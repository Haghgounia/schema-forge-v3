# Excel Comparison Phase 5.1 - DBMS-neutral metadata boundary

## Decision

Artifact routing, comparison, and Excel generation do not select Oracle directly.
The configured `schemaforge.generation.default-database` is resolved to a
`DatabaseProduct`, and a matching `DatabaseMetadataReader` is obtained from a registry.

## Dependency direction

```
ArtifactGenerationService
  -> DatabaseMetadataReaderRegistry
      -> DatabaseMetadataReader (port)
          <- OracleTableMetadataLookup (adapter)
          <- future PostgreSqlTableMetadataReader (adapter)
```

The comparison workbook continues to receive canonical `Table` objects; therefore it
contains no Oracle catalog SQL, PostgreSQL catalog SQL, JDBC type, or vendor class.

## Output rule

- Live table absent: generate SQL using the configured database product.
- Live table present: generate only the comparison XLSX.
- No reader configured: treat the table as absent and generate SQL for the configured product.

## Remaining work

Phase 5.2 will complete the PostgreSQL single-table metadata adapter (comments,
constraints and indexes), then the comparison model will be separated from the XLSX writer.
