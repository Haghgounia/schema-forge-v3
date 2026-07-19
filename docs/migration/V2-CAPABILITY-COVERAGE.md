# SchemaForge v2 to v3 Capability Coverage Baseline

This file prevents incremental work from silently omitting earlier requirements. A capability may move to **Completed** only after implementation and regression tests exist.

| Capability from v2 | v3 target package | Initial state |
|---|---|---|
| DOCX table specification parsing | `specification.adapter.docx` | Contract/skeleton created; implementation migration pending |
| Persian/English header dictionary | `resources/config/schemaforge-dictionary-fa.yml` | Configuration copied; reader migration pending |
| Standard audit column injection | `resources/config/schemaforge-standard.yml`, specification service | Configuration copied; implementation migration pending |
| Enterprise Architect XMI parsing | `specification.adapter.ea` | Contract/skeleton created; implementation migration pending |
| Canonical table/column/PK/FK/index/sequence model | `specification.domain` | Initial v3 model created |
| Duplicate and structural validation | `validation.core` | Initial duplicate validation created; remaining v2 rules pending |
| Spelling validation | `validation.spelling` | Package reserved; migration pending |
| Oracle DDL generation | `generation.oracle` | Provider slot reserved; migration pending |
| DB2 for z/OS DDL generation | `generation.db2zos` | Provider slot reserved; migration pending |
| PostgreSQL provider | `generation.postgresql` | Extension slot reserved |
| SQL Server provider | `generation.sqlserver` | Extension slot reserved |
| MySQL provider | `generation.mysql` | Extension slot reserved |
| SQL, rollback, JSON, text, manifest artifacts | `generation.spi`, `generation.artifact` | Typed artifact model created |
| ZIP packaging in memory | `packaging` | Initial implementation created |
| DBA hints in SQL output | provider generators / generation options | Option modeled; implementation pending |
| Separate SQL and supplementary output per input file | generation result/artifact pipeline | Contract supports it; orchestration pending |
| Batch input and ZIP response | API/application/packaging | Architecture supports it; migration pending |
| Schema report generation | `reporting` | Package reserved; migration pending |
| Schema comparison Excel report | `reporting` | Package reserved; migration pending |
| Oracle metadata inspection | `database.spi`, `database.oracle` | SPI created; JDBC implementation migration pending |
| Database object state and compare model | `database.domain` | Initial result model created; detailed state migration pending |
| REST endpoints | `api` | Status endpoint created; functional endpoints pending |
| Swagger/OpenAPI | configuration and dependencies | Enabled with v2 paths/settings |
| Oracle connection configuration | resources/config and application.yaml | v2 defaults copied; secure external overrides retained |
| Tablespace mapping per schema | application.yaml/provider config | v2 BIM/DPS/LON mappings copied |
| Upload size 500 MB | application.yaml | Preserved |
| Server port 9090 | application.yaml | Preserved |
| Golden-file regression tests | `src/test/resources/golden-files` | Directories created; baseline files pending |

## Completion rule

No phase is complete merely because classes compile. It is complete only when:

1. behavior is implemented;
2. v2 inputs are covered;
3. expected artifacts are asserted;
4. errors and warnings are asserted;
5. `./mvnw clean test` passes;
6. this matrix is updated.
