# SchemaForge V3 Class Catalog

## Discovery

| Class | Description |
|---|---|
| `DiscoveryEngine` | Loads canonical database metadata once and executes all registered discovery rules. |
| `DiscoveryContext` | Carries the document table and inspected canonical database schema to every rule. |
| `DiscoveryRule` | DBMS-neutral contract implemented by each discovery validation rule. |
| `DiscoveryResult` | Immutable aggregate of discovery issues and severity counters. |
| `DiscoveryIssue` | Structured advisory or validation finding with code, location and details. |
| `DiscoverySeverity` | Defines `INFO`, `WARNING` and `ERROR` levels. |
| `DiscoveryCategory` | Classifies field usage, type, length, nullable, default, naming and spelling findings. |
| `FieldUsageRule` | Finds all existing tables that use each document column name. |
| `DataTypeConsistencyRule` | Compares the document type with the most frequent existing canonical type and creates a warning on mismatch. |

## Database Metadata

| Class | Description |
|---|---|
| `DatabaseMetadataProvider` | DBMS-neutral SPI for inspecting a live database as a canonical schema. |
| `OracleMetadataProvider` | Oracle adapter contract that hides Oracle dictionary access. |
| `JdbcOracleMetadataProvider` | JDBC implementation of Oracle metadata discovery. |

## Reporting and Generation

| Class | Description |
|---|---|
| `SchemaCompareExcelWriter` | Creates the canonical document-versus-database comparison workbook. |
| `OracleHintBuilder` | Builds Oracle SQL hints from dictionary and usage metadata. |
