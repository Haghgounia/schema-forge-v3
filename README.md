# SchemaForge v3

SchemaForge v3 is the new single-module Maven baseline for schema specification parsing, validation, DBMS-neutral generation, database inspection, reporting, and artifact packaging.

## Technology baseline

- Java 21
- Spring Boot 3.5.0
- Maven single module
- Spring MVC / Bean Validation / JDBC
- Apache POI 5.4.1
- Springdoc OpenAPI 2.8.13
- Oracle JDBC runtime driver
- JUnit 5 and ArchUnit

## Build

```bash
./mvnw clean test
```

## Run

```bash
./mvnw spring-boot:run
```

- Status: `GET /api/system/status`
- Swagger UI: `/swagger-ui.html`

## Important

The project contains stable architectural contracts and migration targets. Provider and parser implementations marked with `UnsupportedOperationException` are intentionally not presented as completed functionality. Their v2 behavior must be migrated with golden-file regression tests before being considered complete.

See `docs/migration/V2-CAPABILITY-COVERAGE.md` before starting any implementation increment.

## Frontend

پروژه رابط کاربری در دایرکتوری `frontend/` قرار دارد و با React، TypeScript و Vite توسعه داده می‌شود. این بخش مستقل از Maven است و از مسیر `/api` به Backend متصل می‌شود.

```bash
cd frontend
npm install
npm run dev
```

Backend به‌صورت پیش‌فرض روی پورت `9090` و Frontend روی پورت `5173` اجرا می‌شود.

## Backend foundation status

The backend can start independently of Node.js and independently of an Oracle connection:

```cmd
mvnw.cmd spring-boot:run
```

Status: `http://localhost:9090/api/system/status`  
Swagger: `http://localhost:9090/swagger-ui.html`

Frontend installation is not required for backend development.

## Oracle inspection

Oracle integration is disabled by default, so the application can start without an Oracle instance.
Enable it with the following environment variables:

```text
SCHEMAFORGE_ORACLE_ENABLED=true
SCHEMAFORGE_ORACLE_URL=jdbc:oracle:thin:@//localhost:1521/FREEPDB1
SCHEMAFORGE_ORACLE_USERNAME=system
SCHEMAFORGE_ORACLE_PASSWORD=your-password
SCHEMAFORGE_ORACLE_QUERY_TIMEOUT_SECONDS=60
```

Inspection endpoints:

```text
GET /api/database/inspection?databaseType=ORACLE&schemaName=BIM
GET /api/database/inspection/summary?databaseType=ORACLE&schemaName=BIM
```

The Oracle user needs dictionary visibility for the requested schema. Access to `DBA_TABLESPACES` and `V$RESERVED_WORDS` is required only for the related validation/dictionary operations.
