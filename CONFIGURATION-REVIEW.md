# YAML configuration review

## Consolidated into application.yaml

- Oracle connection settings from `config/schemaforge-connection.yml`
- Batch DOCX/DDL settings from `config/ddl-generation.yml`
- Existing web, generation, workspace, Oracle schema, server and SpringDoc settings

Secrets and machine-specific values use environment-variable placeholders.

## Removed

- `config/schemaforge-connection.yml`: duplicated `schemaforge.oracle` and contained hard-coded credentials.
- `config/ddl-generation.yml`: application/test runtime configuration now belongs under `schemaforge.ddl` in `application.yaml`.
- `config/schemaforge-standard.yml`: no Java configuration class or loader references `schemaforge.standards`; keeping it implied behavior that does not exist.

## Kept but currently unused

- `config/schemaforge-dictionary-fa.yml`: static Persian DOCX header vocabulary. It is not currently imported or consumed by any Java class. Keep it only if a dictionary loader will be implemented; otherwise it can be removed.

## Notes

- `spring.jackson.default-property-inclusion`, multipart limits, server error policy and SpringDoc settings are valid application-level settings.
- `schemaforge.build.version` is build metadata and is valid in `application.yaml`.
- Database passwords should never have literal defaults in source-controlled YAML.
- Batch input/output paths are intentionally empty by default and the batch test is disabled by default.

## Oracle dictionary validation and DDL annotations

When `schemaforge.oracle.enabled=true`, Oracle dictionary metadata is loaded once at startup and used by Oracle DDL generation:

- `V$RESERVED_WORDS`: reports an error comment when a document column name is an Oracle reserved word.
- `ALL_TAB_COLUMNS` usage counts: renders `/*  60*/` before each column definition.
- `ALL_TAB_COLUMNS` datatype distribution: if the column name already exists, compares the document datatype with the most frequently used Oracle datatype and renders a warning on mismatch. A column name not found in Oracle is accepted without a datatype warning.

If Oracle is unavailable, processing continues. Usage counts become `0`, reserved-word metadata is empty, and datatype metadata validation is skipped.
