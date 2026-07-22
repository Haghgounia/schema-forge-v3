# SchemaForge v3 - Comparison Engine Design

Version: schema-forge-v3-2026-07-22-1812

## Purpose

Comparison Engine determines differences between expected schema
definition and live database metadata.

## Main Flow

    Document Specification
            |
            v
    Canonical Table Model
            |
            v
    Comparison Engine
            |
            v
    Comparison Difference List

## Comparison Rules

Rules are separated by responsibility:

-   Column existence
-   Column definition
-   Primary key
-   Foreign key
-   Unique key
-   Index
-   Comments

## Difference Model

Each difference contains:

-   Scope
-   Type
-   Severity
-   Resolution Strategy
-   Expected Value
-   Actual Value

## Design Benefits

-   Extensible rule model
-   Database independent comparison
-   Reduced false positives through normalization
