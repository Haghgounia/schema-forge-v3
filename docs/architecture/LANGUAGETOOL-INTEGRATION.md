# LanguageTool integration

SchemaForge uses LanguageTool as an optional advisory validator for database identifiers.
It does not auto-correct table or column names.

## Design decisions

- Disabled by default.
- Fail-open by default: unavailable LanguageTool must not stop DDL generation.
- Column identifiers are converted from snake/kebab case to words only for checking.
- Configured technical abbreviations are ignored.
- Suggestions are bounded by `maximum-suggestions`.
- Results are cached in memory by language and normalized text.
- Findings are emitted with `SPELLING_WARNING` severity `WARNING`.

## Production recommendation

Use a private/self-hosted LanguageTool service for batch processing. The public endpoint is
appropriate for development and limited testing, not for hundreds of documents and thousands
of identifiers.

## Configuration

```yaml
schemaforge:
  spell-check:
    enabled: false
    endpoint: https://api.languagetool.org/v2/check
    language: en-US
    connect-timeout: 3s
    request-timeout: 5s
    maximum-suggestions: 3
    fail-open: true
    cache-ttl: 24h
    technical-terms:
      - ID
      - UUID
      - IBAN
      - SWIFT
      - CIF
      - GL
      - FK
      - PK
```
