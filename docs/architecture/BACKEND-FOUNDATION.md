# SchemaForge v3 Backend Foundation

This increment establishes the runtime foundation without migrating Oracle or DB2 generation logic.

## Included

- Java 21 / Spring Boot single-module backend
- DBMS-neutral canonical model and generation SPI
- Dialect and parser registries
- Parse specification application service
- Typed `schemaforge.*` configuration properties
- REST response envelope and centralized error response
- Request correlation ID propagation
- CORS configuration for the future React UI
- Swagger/OpenAPI endpoint
- Backend starts without requiring an Oracle connection
- Unit and web integration tests

## Runtime endpoints

- `GET /api/system/status`
- `/swagger-ui.html`
- `/v3/api-docs`

## Deliberately deferred

- Migration of the complete DOCX parser
- Migration of the EA XMI parser
- Oracle and DB2 z/OS providers
- database metadata inspection
- batch generation, reports and comparison

These remain tracked in `docs/migration/V2-CAPABILITY-COVERAGE.md` and must not be marked complete until regression tests pass.
