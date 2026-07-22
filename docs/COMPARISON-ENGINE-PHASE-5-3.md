# SchemaForge Phase 5.3 - Comparison Engine Core and Column Rules

## Scope

This phase introduces the DBMS-independent comparison core without changing the current Excel writer or DDL generation flow.

Implemented:

- Immutable comparison report model
- Difference scope, type, severity and resolution strategy
- Comparison summary
- Comparison context and O(1) column lookup
- Ordered comparison-rule SPI
- Schema comparison engine
- Column existence comparison
- Column definition comparison
- Data type name, length, precision, scale, nullable, default, identity and comment rules
- Conservative default-value normalization
- Unit tests for normalizers, rules and engine

Not changed:

- ArtifactGenerationService runtime flow
- CanonicalSchemaCompareExcelWriter
- Oracle metadata reader
- DDL generators
- Existing report format

## Runtime architecture

```text
Document Table + Database Table
             |
             v
    ComparisonContextFactory
             |
             v
      ComparisonContext
             |
             v
   Ordered ComparisonRule list
             |
             v
   SchemaComparisonEngine
             |
             v
   TableComparisonReport
```

## Important design rules

1. Comparison rules consume only canonical domain models through ComparisonContext.
2. A rule does not call another top-level rule.
3. Column property rules return at most one difference for one property.
4. Writers do not perform comparison in the new architecture.
5. Existing Excel comparison remains untouched until the adapter phase.
6. Missing and extra columns are never hidden by rename detection.

## Packages

```text
com.behsazan.schemaforge.comparison.model
com.behsazan.schemaforge.comparison.context
com.behsazan.schemaforge.comparison.engine
com.behsazan.schemaforge.comparison.rule
com.behsazan.schemaforge.comparison.column
com.behsazan.schemaforge.comparison.normalizer
com.behsazan.schemaforge.comparison.support
```

## Verification

The new production classes were compiled successfully with Java 21 using `javac` against the canonical domain model.

Full Maven tests could not be executed in the offline build environment because Maven Wrapper attempted to download Maven 3.9.16 from Maven Central.

## Next phase

Phase 5.3.3 adds canonical signatures and independent comparison rules for:

- Primary key
- Unique constraints
- Foreign keys
- Check constraints

After constraint and index rules are complete, the current Excel writer will be migrated through an adapter to consume `TableComparisonReport`.
