# SchemaForge v3 Architecture Baseline

## Style

Single Maven module, feature-oriented packages, canonical domain model, ports and adapters, and plugin-style DBMS providers.

## Dependency direction

```text
api/configuration -> application -> specification/validation/generation SPI
                                      ^
                                      |
                              DBMS provider packages
```

Rules:

1. `specification.domain` is pure Java and DBMS-neutral.
2. Parser adapters only produce canonical models; they do not generate SQL.
3. Core selects a provider through `DatabaseDialect` and `DialectRegistry`.
4. Provider-specific physical options stay behind `DatabaseOptions` or generation options.
5. Generated output is always represented as a list of typed artifacts.
6. All migrated v2 behavior requires regression or golden-file tests.
7. No `if (databaseType == ...)` branches are permitted in core orchestration.
