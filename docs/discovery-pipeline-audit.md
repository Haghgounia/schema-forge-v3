# Discovery Pipeline Audit

## Scope

This audit covers the discovery rules that can add advisory comments to generated CREATE TABLE SQL.

## Findings

| Rule | Spring component | Unit test | Invoked by DiscoveryEngine | Included in SQL |
|---|---:|---:|---:|---:|
| DataTypeConsistencyRule | Yes | Yes | Yes | Warning only when different |
| LengthConsistencyRule | Yes | Yes | Yes | Warning only when different |
| DefaultConsistencyRule | Yes | Yes | Yes | Warning only when different |
| NullableConsistencyRule | Yes | Yes | Yes | Warning only when different |
| FieldUsageRule | Yes | Yes | Yes | No; INFO messages are intentionally omitted |

All rule implementations are injected as `List<DiscoveryRule>` into `DiscoveryEngine`. Therefore, they were already implemented and are executed automatically once `DiscoveryEngine` is connected to `ArtifactGenerationService`.

## Required behavior for data type consistency

1. A column name with no previous database usage produces no SQL message.
2. A column whose document type matches the existing standard produces no SQL message.
3. A mismatch produces a warning and does not stop SQL generation.
4. Informational field-usage messages are not written to SQL.
5. A discovery/database failure does not stop CREATE TABLE SQL generation.

## Remaining gaps

- Precision and scale do not have dedicated discovery rules; they are currently part of the data-type signature.
- The SQL warning action text is generic for all rule categories.
- Discovery failure is currently fail-open and silent; operational logging can be added separately.
