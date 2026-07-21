# Phase 3.6 - Robust Parser & Recovery Engine

## Pipeline

```text
DOCX cell
  -> text cleanup
  -> DataTypeNormalizer / IdentifierSanitizer
  -> canonical parser
  -> canonical schema model
  -> DDL engine
```

## Recovery policy

The recovery layer is conservative. It selects the first recognizable datatype when a cell contains
multiple values, repairs common punctuation defects, and records each material correction as a warning.
It does not silently discard recovery information.

Warnings are attached to `DatabaseSchema` metadata under `recovery.warningCount` and
`recovery.warnings` so downstream reports can expose them.

## Supported datatype recoveries

- `TIME_STAMP` -> `TIMESTAMP`
- `NUMBER2(1)` -> `NUMBER(2,1)`
- `NUMBER9,0)` -> `NUMBER(9,0)`
- `NUMBER(10.0)` -> `NUMBER(10,0)`
- `NUMBER(10(` -> `NUMBER(10)`
- `NUMBER(4) NUMBER(2)` -> first recognizable datatype
- trailing notes after a recognizable datatype -> recognizable datatype
- unrecognized textual datatype cells -> `VARCHAR2(4000)` with a warning
- non-positive NUMBER precision -> minimum precision of 1 with a warning

## Identifier recovery

Whitespace, dots and hyphens are converted to underscores. Characters outside the unquoted identifier
character set are removed. Identifiers are upper-cased, forced to start with a letter, and limited to
128 characters. Every changed identifier produces a warning.

## Non-recoverable documents

Documents without a recognizable column specification table remain non-recoverable and are reported as
skipped/error inputs. Fabricating an entire table definition is outside the recovery policy.
