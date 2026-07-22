# SchemaForge v3 - Testing Strategy

Version: schema-forge-v3-2026-07-22-1812

## Overview

Testing strategy ensures correctness of schema parsing, comparison,
generation and reporting.

# Test Layers

## Unit Tests

Purpose:

Validate isolated components.

Examples:

-   Comparison Rules
-   Normalizers
-   Validators
-   DDL Generators

## Integration Tests

Purpose:

Validate interaction between components.

Examples:

-   Oracle metadata comparison
-   DOCX parsing end-to-end
-   Artifact generation

## Main Test Areas

### Specification Tests

Validate:

-   DOCX parsing
-   Table extraction
-   Column extraction

### Comparison Tests

Validate:

-   Missing columns
-   Extra columns
-   Type changes
-   Constraint changes

### Generation Tests

Validate:

-   SQL creation
-   Constraint generation
-   Index generation

### Reporting Tests

Validate:

-   Excel creation
-   Difference display
-   Formatting

## Regression Strategy

Every new database rule or generation feature should include regression
tests.
