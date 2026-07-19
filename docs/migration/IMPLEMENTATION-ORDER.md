# Implementation Order

1. Freeze representative v2 inputs and outputs as golden files.
2. Migrate configuration readers and canonical validation.
3. Migrate DOCX parser and audit-column injector.
4. Migrate Oracle provider without output changes.
5. Migrate artifact, report, ZIP, and batch orchestration.
6. Migrate Oracle database inspection and schema comparison.
7. Migrate EA XMI parser.
8. Complete DB2 for z/OS provider.
9. Add new providers only after Oracle and DB2 regression suites pass.

Each increment must preserve all previously completed matrix rows.
