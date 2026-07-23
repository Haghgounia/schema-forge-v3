# Oracle Virtual Column Audit

## Repository audit

No existing `VirtualColumnGenerator`, `GeneratedColumnGenerator`, renderer, parser field, or test was found.
The existing Oracle column pipeline was reused:

- `Column`
- `DocxSpecificationParser`
- `OracleColumnDefinitionGenerator`
- `TableDdlGenerator`

No new generator class or parallel pipeline was introduced.

## DOCX input

A virtual column expression may be supplied in a column-table field whose header is one of:

- `VIRTUAL COLUMN EXPRESSION`
- `VIRTUAL EXPRESSION`
- `GENERATED EXPRESSION`
- `COLUMN EXPRESSION`
- `عبارت ستون مجازی`
- `ستون مجازی`

Example value:

```text
CREDIT_AMOUNT - DEBIT_AMOUNT
```

## Oracle output

```sql
NET_AMOUNT NUMBER(18,2)
    GENERATED ALWAYS AS (CREDIT_AMOUNT - DEBIT_AMOUNT) VIRTUAL
```

## Validation

A generated column cannot simultaneously define:

- identity semantics
- a default expression

A generated expression must not end with a semicolon.

## PostgreSQL boundary

PostgreSQL generation currently fails explicitly for this canonical feature instead of silently emitting an ordinary column. PostgreSQL-specific generated-column syntax will be implemented in its own phase.
