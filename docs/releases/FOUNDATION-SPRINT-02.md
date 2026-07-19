# SchemaForge v3 - Foundation Sprint 02

## Scope
Canonical, DBMS-neutral domain model and migration of parser/generation contracts to that model.

## New packages
- `com.behsazan.schemaforge.domain.model`
- `com.behsazan.schemaforge.domain.valueobject`
- `com.behsazan.schemaforge.domain.enums`

## Main additions
- Aggregate roots: `Project`, `Specification`, `DatabaseSchema`, `Table`
- Schema objects: columns, keys, checks, indexes, sequences, views, synonyms, triggers and routines
- Value objects with validation: `Identifier`, `QualifiedName`, `DataType`, `Description`, `DefaultValue`
- Builder APIs for `DatabaseSchema` and `Table`
- Invariant validation for duplicate objects, missing constraint columns and invalid data-type dimensions
- Canonical model tests

## Modified contracts
- `SpecificationParser` now returns `DatabaseSchema`
- `ParseSpecificationService` now returns `DatabaseSchema`
- `GenerationContext` now accepts `DatabaseSchema`

## Compatibility note
The earlier classes under `specification.domain` are retained temporarily for migration reference, but new implementation code must use `domain.model`.
